package com.example.authbackend.service.impl;

import com.example.authbackend.dto.request.LoginRequest;
import com.example.authbackend.dto.request.RegisterRequest;
import com.example.authbackend.dto.response.ApiResponse;
import com.example.authbackend.dto.response.AuthResponse;
import com.example.authbackend.dto.response.UserResponse;
import com.example.authbackend.entity.*;
import com.example.authbackend.exception.EmailAlreadyExistsException;
import com.example.authbackend.exception.InvalidTokenException;
import com.example.authbackend.exception.UserNotFoundException;
import com.example.authbackend.repository.EmailVerificationTokenRepository;
import com.example.authbackend.repository.RoleRepository;
import com.example.authbackend.repository.UserRepository;
import com.example.authbackend.security.JwtTokenProvider;
import com.example.authbackend.service.AuthService;
import com.example.authbackend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private EmailVerificationTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${app.email.verification.expiration}")
    private long emailVerificationExpirationMs;
    
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(loginRequest.getEmail());
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        UserResponse userResponse = mapToUserResponse(user);
        
        logger.info("User {} logged in successfully", loginRequest.getEmail());
        
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }
    
    @Override
    public ApiResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email address already in use!");
        }
        
        // Create new user
        User user = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
        );
        
        Role userRole = roleRepository.findByName(RoleName.USER)
            .orElseThrow(() -> new RuntimeException("User Role not set."));
        
        user.setRoles(Collections.singleton(userRole));
        user.setProvider(AuthProvider.LOCAL);
        user.setEmailVerified(false);
        
        User savedUser = userRepository.save(user);
        
        // Generate email verification token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
            token,
            savedUser,
            LocalDateTime.now().plusSeconds(emailVerificationExpirationMs / 1000)
        );
        
        tokenRepository.save(verificationToken);
        
        // Send verification email
        emailService.sendEmailVerification(savedUser, token);
        
        logger.info("User {} registered successfully", registerRequest.getEmail());
        
        return new ApiResponse(true, "User registered successfully. Please check your email for verification.");
    }
    
    @Override
    public ApiResponse verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));
        
        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new InvalidTokenException("Verification token has expired");
        }
        
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        
        tokenRepository.delete(verificationToken);
        
        // Send welcome email
        emailService.sendWelcomeEmail(user);
        
        logger.info("Email verified successfully for user {}", user.getEmail());
        
        return new ApiResponse(true, "Email verified successfully!");
    }
    
    @Override
    public ApiResponse resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        if (user.getEmailVerified()) {
            return new ApiResponse(false, "Email is already verified");
        }
        
        // Delete any existing verification tokens
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        // Generate new verification token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
            token,
            user,
            LocalDateTime.now().plusSeconds(emailVerificationExpirationMs / 1000)
        );
        
        tokenRepository.save(verificationToken);
        
        // Send verification email
        emailService.sendEmailVerification(user, token);
        
        logger.info("Verification email resent to {}", email);
        
        return new ApiResponse(true, "Verification email sent successfully");
    }
    
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        
        String email = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        String newAccessToken = tokenProvider.generateTokenFromUsername(email);
        String newRefreshToken = tokenProvider.generateRefreshToken(email);
        
        UserResponse userResponse = mapToUserResponse(user);
        
        return new AuthResponse(newAccessToken, newRefreshToken, userResponse);
    }
    
    @Override
    public ApiResponse logout(String token) {
        // In a production environment, you might want to blacklist the token
        // For now, we'll just return a success response
        logger.info("User logged out successfully");
        return new ApiResponse(true, "Logged out successfully");
    }
    
    private UserResponse mapToUserResponse(User user) {
        Set<String> roles = new HashSet<>();
        if (user.getRoles() != null) {
            roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        }
        
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getEmailVerified(),
            user.getProvider().name(),
            user.getImageUrl(),
            user.getCreatedAt(),
            roles
        );
    }
}
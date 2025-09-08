// AuthServiceTest.java
package com.example.authbackend.service.impl;

import com.example.authbackend.dto.request.RegisterRequest;
import com.example.authbackend.dto.response.ApiResponse;
import com.example.authbackend.entity.Role;
import com.example.authbackend.entity.RoleName;
import com.example.authbackend.entity.User;
import com.example.authbackend.exception.EmailAlreadyExistsException;
import com.example.authbackend.repository.EmailVerificationTokenRepository;
import com.example.authbackend.repository.RoleRepository;
import com.example.authbackend.repository.UserRepository;
import com.example.authbackend.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private EmailVerificationTokenRepository tokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    private RegisterRequest registerRequest;
    private Role userRole;
    
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
            "John", "Doe", "john@example.com", "password123"
        );
        
        userRole = new Role(RoleName.USER);
    }
    
    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        
        ApiResponse response = authService.register(registerRequest);
        
        assertTrue(response.getSuccess());
        assertEquals("User registered successfully. Please check your email for verification.", response.getMessage());
        
        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any());
        verify(emailService).sendEmailVerification(any(User.class), anyString());
    }
    
    @Test
    void testRegisterEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });
        
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendEmailVerification(any(User.class), anyString());
    }
}

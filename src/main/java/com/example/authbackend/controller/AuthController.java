package com.example.authbackend.controller;

import com.example.authbackend.dto.request.LoginRequest;
import com.example.authbackend.dto.request.RegisterRequest;
import com.example.authbackend.dto.response.ApiResponse;
import com.example.authbackend.dto.response.AuthResponse;
import com.example.authbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration attempt for email: {}", registerRequest.getEmail());
        ApiResponse apiResponse = authService.register(registerRequest);
        return ResponseEntity.ok(apiResponse);
    }
    
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token") String token) {
        logger.info("Email verification attempt with token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
        ApiResponse apiResponse = authService.verifyEmail(token);
        return ResponseEntity.ok(apiResponse);
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerificationEmail(@RequestParam("email") String email) {
        logger.info("Resend verification email request for: {}", email);
        ApiResponse apiResponse = authService.resendVerificationEmail(email);
        return ResponseEntity.ok(apiResponse);
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        logger.info("Refresh token request");
        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }
        
        logger.info("Logout request");
        ApiResponse apiResponse = authService.logout(token);
        return ResponseEntity.ok(apiResponse);
    }
    
    // Inner class for refresh token request
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public RefreshTokenRequest() {}
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
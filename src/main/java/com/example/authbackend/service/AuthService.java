// AuthService.java
package com.example.authbackend.service;

import com.example.authbackend.dto.request.LoginRequest;
import com.example.authbackend.dto.request.RegisterRequest;
import com.example.authbackend.dto.response.AuthResponse;
import com.example.authbackend.dto.response.ApiResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    ApiResponse register(RegisterRequest registerRequest);
    ApiResponse verifyEmail(String token);
    ApiResponse resendVerificationEmail(String email);
    AuthResponse refreshToken(String refreshToken);
    ApiResponse logout(String token);
}
package com.example.authbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.authbackend.dto.request.LoginRequest;
import com.example.authbackend.dto.request.RegisterRequest;
import com.example.authbackend.dto.response.ApiResponse;
import com.example.authbackend.dto.response.AuthResponse;
import com.example.authbackend.dto.response.UserResponse;
import com.example.authbackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "John", "Doe", "john@example.com", "password123"
        );
        
        ApiResponse apiResponse = new ApiResponse(true, "User registered successfully");
        
        when(authService.register(any(RegisterRequest.class))).thenReturn(apiResponse);
        
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }
    
    @Test
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password123");
        
        UserResponse userResponse = new UserResponse(
            1L, "John", "Doe", "john@example.com", true, "LOCAL", null, 
            LocalDateTime.now(), Set.of("USER")
        );
        
        AuthResponse authResponse = new AuthResponse("access-token", "refresh-token", userResponse);
        
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);
        
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"));
    }
    
    @Test
    void testVerifyEmail() throws Exception {
        String token = "verification-token";
        ApiResponse apiResponse = new ApiResponse(true, "Email verified successfully!");
        
        when(authService.verifyEmail(token)).thenReturn(apiResponse);
        
        mockMvc.perform(get("/auth/verify-email")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully!"));
    }
}
package com.example.authbackend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.authbackend.dto.request.RegisterRequest;
import com.example.authbackend.entity.Role;
import com.example.authbackend.entity.RoleName;
import com.example.authbackend.repository.RoleRepository;
import com.example.authbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AuthIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        // Ensure roles exist
        if (roleRepository.findByName(RoleName.USER).isEmpty()) {
            roleRepository.save(new Role(RoleName.USER));
        }
        if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
            roleRepository.save(new Role(RoleName.ADMIN));
        }
    }
    
    @Test
    void testCompleteRegistrationFlow() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
            "Integration", "Test", "integration@test.com", "password123"
        );
        
        // Test registration
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully. Please check your email for verification."));
        
        // Verify user exists in database
        assertTrue(userRepository.existsByEmail("integration@test.com"));
    }
    
    @Test
    void testRegistrationWithExistingEmail() throws Exception {
        // Register first user
        RegisterRequest firstRequest = new RegisterRequest(
            "First", "User", "existing@test.com", "password123"
        );
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());
        
        // Try to register with same email
        RegisterRequest duplicateRequest = new RegisterRequest(
            "Second", "User", "existing@test.com", "password456"
        );
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email address already in use!"));
    }
}
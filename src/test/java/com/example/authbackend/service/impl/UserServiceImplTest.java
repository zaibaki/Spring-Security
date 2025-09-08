// UserServiceTest.java
package com.example.authbackend.service.impl;

import com.example.authbackend.dto.response.UserResponse;
import com.example.authbackend.entity.AuthProvider;
import com.example.authbackend.entity.Role;
import com.example.authbackend.entity.RoleName;
import com.example.authbackend.entity.User;
import com.example.authbackend.exception.UserNotFoundException;
import com.example.authbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setEmailVerified(true);
        testUser.setProvider(AuthProvider.LOCAL);
        testUser.setCreatedAt(LocalDateTime.now());
        
        Role userRole = new Role(RoleName.USER);
        testUser.setRoles(Set.of(userRole));
    }
    
    @Test
    void testGetUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        
        UserResponse userResponse = userService.getUserById(1L);
        
        assertNotNull(userResponse);
        assertEquals("John", userResponse.getFirstName());
        assertEquals("john@example.com", userResponse.getEmail());
        assertTrue(userResponse.getRoles().contains("USER"));
    }
    
    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }
    
    @Test
    void testChangePasswordSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("new-encoded-password");
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        boolean result = userService.changePassword("john@example.com", "oldPassword", "newPassword");
        
        assertTrue(result);
    }
    
    @Test
    void testChangePasswordWrongOldPassword() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        boolean result = userService.changePassword("john@example.com", "wrongPassword", "newPassword");
        
        assertFalse(result);
    }
}
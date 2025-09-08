// UserServiceImpl.java
package com.example.authbackend.service.impl;

import com.example.authbackend.dto.response.UserResponse;
import com.example.authbackend.entity.User;
import com.example.authbackend.exception.UserNotFoundException;
import com.example.authbackend.repository.UserRepository;
import com.example.authbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        return mapToUserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        return mapToUserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }
    
    @Override
    public UserResponse updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Update fields
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getImageUrl() != null) {
            user.setImageUrl(userDetails.getImageUrl());
        }
        
        User updatedUser = userRepository.save(user);
        logger.info("User {} updated successfully", user.getEmail());
        
        return mapToUserResponse(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        userRepository.delete(user);
        logger.info("User {} deleted successfully", user.getEmail());
    }
    
    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password changed successfully for user {}", email);
        return true;
    }
    
    private UserResponse mapToUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());
        
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
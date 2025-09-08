package com.example.authbackend.controller;

import com.example.authbackend.dto.response.ApiResponse;
import com.example.authbackend.dto.response.UserResponse;
import com.example.authbackend.entity.User;
import com.example.authbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Get current user request for: {}", userDetails.getUsername());
        UserResponse userResponse = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(userResponse);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        logger.info("Get user by ID request for ID: {}", id);
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        logger.info("Get all users request with pageable: {}", pageable);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateRequest) {
        logger.info("Update user request for ID: {}", id);
        
        User userDetails = new User();
        userDetails.setFirstName(updateRequest.getFirstName());
        userDetails.setLastName(updateRequest.getLastName());
        userDetails.setImageUrl(updateRequest.getImageUrl());
        
        UserResponse userResponse = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(userResponse);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        logger.info("Delete user request for ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }
    
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        
        logger.info("Change password request for: {}", userDetails.getUsername());
        
        boolean success = userService.changePassword(
            userDetails.getUsername(),
            changePasswordRequest.getOldPassword(),
            changePasswordRequest.getNewPassword()
        );
        
        if (success) {
            return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid old password"));
        }
    }
    
    // Inner classes for request DTOs
    public static class UserUpdateRequest {
        private String firstName;
        private String lastName;
        private String imageUrl;
        
        public UserUpdateRequest() {}
        
        // Getters and Setters
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
    
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
        public ChangePasswordRequest() {}
        
        // Getters and Setters
        public String getOldPassword() {
            return oldPassword;
        }
        
        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
        
        public String getNewPassword() {
            return newPassword;
        }
        
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
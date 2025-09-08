// UserService.java
package com.example.authbackend.service;

import com.example.authbackend.dto.response.UserResponse;
import com.example.authbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getCurrentUser(String email);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    boolean changePassword(String email, String oldPassword, String newPassword);
}
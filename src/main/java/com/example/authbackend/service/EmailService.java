package com.example.authbackend.service;

import com.example.authbackend.entity.User;

public interface EmailService {
    void sendEmailVerification(User user, String token);
    void sendPasswordResetEmail(User user, String token);
    void sendWelcomeEmail(User user);
}
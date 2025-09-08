package com.example.authbackend.service.impl;

import com.example.authbackend.entity.User;
import com.example.authbackend.service.EmailService;
import com.example.authbackend.util.EmailTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Override
    @Async
    public void sendEmailVerification(User user, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Email Verification - Your App");
            
            String verificationUrl = "http://localhost:" + serverPort + contextPath + "/auth/verify-email?token=" + token;
            String htmlContent = EmailTemplates.getEmailVerificationTemplate(user.getFirstName(), verificationUrl);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Email verification sent to: {}", user.getEmail());
            
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send email verification to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send email verification", e);
        }
    }
    
    @Override
    @Async
    public void sendPasswordResetEmail(User user, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset - Your App");
            
            String resetUrl = "http://localhost:3000/reset-password?token=" + token;
            String htmlContent = EmailTemplates.getPasswordResetTemplate(user.getFirstName(), resetUrl);
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Password reset email sent to: {}", user.getEmail());
            
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to Your App!");
            
            String htmlContent = EmailTemplates.getWelcomeTemplate(user.getFirstName());
            
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Welcome email sent to: {}", user.getEmail());
            
        } catch (MessagingException | MailException e) {
            logger.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }
}
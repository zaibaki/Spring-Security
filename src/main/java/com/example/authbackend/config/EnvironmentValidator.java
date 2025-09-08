package com.example.authbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1) // Run before DataLoader
public class EnvironmentValidator implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentValidator.class);
    
    @Value("${DB_USERNAME:FALLBACK_DETECTED}")
    private String dbUsername;
    
    @Value("${DB_PASSWORD:FALLBACK_DETECTED}")
    private String dbPassword;
    
    @Value("${JWT_SECRET:mySecretKey}")
    private String jwtSecret;
    
    @Value("${SMTP_USERNAME:FALLBACK_DETECTED}")
    private String smtpUsername;
    
    @Value("${SMTP_PASSWORD:FALLBACK_DETECTED}")
    private String smtpPassword;
    
    @Value("${EMAIL_FROM:noreply@yourapp.com}")
    private String emailFrom;
    
    @Value("${GOOGLE_CLIENT_ID:FALLBACK_DETECTED}")
    private String googleClientId;
    
    @Value("${GOOGLE_CLIENT_SECRET:FALLBACK_DETECTED}")
    private String googleClientSecret;
    
    @Override
    public void run(String... args) throws Exception {
        validateEnvironment();
    }
    
    private void validateEnvironment() {
        logger.info("🔍 Validating environment configuration...");
        
        // Check if .env file exists
        File envFile = new File(".env");
        if (!envFile.exists()) {
            logger.warn("⚠️  .env file not found in project root! Create one using .env.example as template");
        } else {
            logger.info("✅ .env file found");
        }
        
        List<String> missingVars = new ArrayList<>();
        List<String> fallbackVars = new ArrayList<>();
        
        // Check each environment variable
        checkVariable("DB_USERNAME", dbUsername, missingVars, fallbackVars);
        checkVariable("DB_PASSWORD", dbPassword, missingVars, fallbackVars);
        checkVariable("JWT_SECRET", jwtSecret, missingVars, fallbackVars, "mySecretKey");
        checkVariable("SMTP_USERNAME", smtpUsername, missingVars, fallbackVars);
        checkVariable("SMTP_PASSWORD", smtpPassword, missingVars, fallbackVars);
        checkVariable("EMAIL_FROM", emailFrom, missingVars, fallbackVars, "noreply@yourapp.com");
        checkVariable("GOOGLE_CLIENT_ID", googleClientId, missingVars, fallbackVars);
        checkVariable("GOOGLE_CLIENT_SECRET", googleClientSecret, missingVars, fallbackVars);
        
        // Report results
        if (missingVars.isEmpty() && fallbackVars.isEmpty()) {
            logger.info("✅ All environment variables are properly configured!");
        } else {
            if (!missingVars.isEmpty()) {
                logger.error("❌ Missing required environment variables: {}", String.join(", ", missingVars));
                logger.error("💡 Add these to your .env file or set as system environment variables");
            }
            
            if (!fallbackVars.isEmpty()) {
                logger.warn("⚠️  Using fallback values for: {}", String.join(", ", fallbackVars));
                logger.warn("💡 Consider setting proper values in .env file for production use");
            }
        }
        
        // Security warnings
        if (jwtSecret.equals("mySecretKey")) {
            logger.error("🚨 SECURITY WARNING: Using default JWT secret! Change JWT_SECRET in .env file");
        }
        
        if (emailFrom.contains("yourapp.com")) {
            logger.warn("📧 Using default email sender. Update EMAIL_FROM in .env file");
        }
        
        logger.info("🏁 Environment validation complete");
    }
    
    private void checkVariable(String name, String value, List<String> missing, List<String> fallback) {
        checkVariable(name, value, missing, fallback, null);
    }
    
    private void checkVariable(String name, String value, List<String> missing, List<String> fallback, String defaultValue) {
        if (value.equals("FALLBACK_DETECTED")) {
            missing.add(name);
        } else if (defaultValue != null && value.equals(defaultValue)) {
            fallback.add(name);
        }
    }
}
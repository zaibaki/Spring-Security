package com.example.authbackend.config;

import com.example.authbackend.entity.Role;
import com.example.authbackend.entity.RoleName;
import com.example.authbackend.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        loadRoles();
    }
    
    private void loadRoles() {
        if (roleRepository.count() == 0) {
            logger.info("Loading default roles...");
            
            Role userRole = new Role(RoleName.USER);
            Role adminRole = new Role(RoleName.ADMIN);
            Role moderatorRole = new Role(RoleName.MODERATOR);
            
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            roleRepository.save(moderatorRole);
            
            logger.info("Default roles loaded successfully");
        } else {
            logger.info("Roles already exist, skipping initialization");
        }
    }
}
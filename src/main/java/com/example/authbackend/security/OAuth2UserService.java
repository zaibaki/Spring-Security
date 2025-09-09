package com.example.authbackend.security;

import com.example.authbackend.entity.AuthProvider;
import com.example.authbackend.entity.Role;
import com.example.authbackend.entity.RoleName;
import com.example.authbackend.entity.User;
import com.example.authbackend.repository.RoleRepository;
import com.example.authbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        try {
            // Get user info from Google
            OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
            
            logger.info("OAuth2 user attributes: {}", oAuth2User.getAttributes());
            
            // Process the user
            User user = processOAuth2User(oAuth2UserRequest, oAuth2User);
            
            // Return UserPrincipal - roles should be loaded now
            return new UserPrincipal(user, oAuth2User.getAttributes());
            
        } catch (Exception ex) {
            logger.error("Error loading OAuth2 user: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException("Error loading OAuth2 user: " + ex.getMessage());
        }
    }
    
    private User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        // Extract user info from Google
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String imageUrl = (String) attributes.get("picture");
        String providerId = (String) attributes.get("sub");
        
        logger.info("Processing OAuth2 user - Email: {}, Name: {} {}", email, firstName, lastName);
        
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        // Check if user already exists - use eager fetching
        Optional<User> userOptional = userRepository.findByEmailWithRoles(email);
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            logger.info("Existing user found: {}", email);
            
            // Update user info if it's from Google or if switching from LOCAL to GOOGLE
            if (user.getProvider() == AuthProvider.GOOGLE || user.getProvider() == AuthProvider.LOCAL) {
                user.setFirstName(firstName != null ? firstName : user.getFirstName());
                // Handle null lastName with fallback to existing value or default
                if (lastName != null) {
                    user.setLastName(lastName);
                } else if (user.getLastName() == null || user.getLastName().isEmpty()) {
                    user.setLastName("User"); // Default value if existing is also empty
                }
                user.setImageUrl(imageUrl);
                
                // If user was LOCAL, convert to GOOGLE
                if (user.getProvider() == AuthProvider.LOCAL) {
                    user.setProvider(AuthProvider.GOOGLE);
                    user.setProviderId(providerId);
                    user.setEmailVerified(true); // Google emails are verified
                    logger.info("Converted LOCAL user to GOOGLE: {}", email);
                } else {
                    logger.info("Updated existing Google user: {}", email);
                }
                
                user = userRepository.save(user);
            } else {
                throw new OAuth2AuthenticationException("User exists with different provider: " + user.getProvider());
            }
        } else {
            // Create new user
            user = createNewUser(email, firstName, lastName, imageUrl, providerId);
            logger.info("Created new Google user: {}", email);
            
            // Fetch the saved user with roles to ensure roles are loaded
            user = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new OAuth2AuthenticationException("User not found after creation"));
        }
        
        return user;
    }
    
    private User createNewUser(String email, String firstName, String lastName, String imageUrl, String providerId) {
        User user = new User();
        
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(providerId);
        user.setFirstName(firstName != null ? firstName : "");
        user.setLastName(lastName != null ? lastName : "");
        user.setEmail(email);
        user.setImageUrl(imageUrl);
        user.setEmailVerified(true); // Google emails are always verified
        
        // Assign USER role
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("User Role not found"));
        
        user.setRoles(Collections.singleton(userRole));
        
        return userRepository.save(user);
    }
}
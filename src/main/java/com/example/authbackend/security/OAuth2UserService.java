package com.example.authbackend.security;

import com.example.authbackend.entity.AuthProvider;
import com.example.authbackend.entity.Role;
import com.example.authbackend.entity.RoleName;
import com.example.authbackend.entity.User;
import com.example.authbackend.exception.EmailAlreadyExistsException;
import com.example.authbackend.repository.RoleRepository;
import com.example.authbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            oAuth2UserRequest.getClientRegistration().getRegistrationId(), 
            oAuth2User.getAttributes()
        );
        
        if (!StringUtils.hasLength(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(
                oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
                throw new EmailAlreadyExistsException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        
        return new UserPrincipal(user, oAuth2User.getAttributes());
    }
    
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setEmailVerified(true);
        
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("User Role not set."));
        
        user.setRoles(Collections.singleton(userRole));
        
        return userRepository.save(user);
    }
    
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }
}

// OAuth2UserInfo.java
abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;
    
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public abstract String getId();
    public abstract String getFirstName();
    public abstract String getLastName();
    public abstract String getEmail();
    public abstract String getImageUrl();
}

// GoogleOAuth2UserInfo.java
class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }
    
    @Override
    public String getFirstName() {
        return (String) attributes.get("given_name");
    }
    
    @Override
    public String getLastName() {
        return (String) attributes.get("family_name");
    }
    
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}

// OAuth2UserInfoFactory.java
class OAuth2UserInfoFactory {
    
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
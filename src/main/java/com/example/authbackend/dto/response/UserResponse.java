// UserResponse.java
package com.example.authbackend.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private String provider;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Set<String> roles;
    
    public UserResponse() {}
    
    public UserResponse(Long id, String firstName, String lastName, String email, 
                       Boolean emailVerified, String provider, String imageUrl, 
                       LocalDateTime createdAt, Set<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.emailVerified = emailVerified;
        this.provider = provider;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.roles = roles;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}

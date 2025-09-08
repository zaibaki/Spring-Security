// JwtTokenProviderTest.java
package com.example.authbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    
    private JwtTokenProvider jwtTokenProvider;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "mySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 86400000);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationInMs", 604800000);
    }
    
    @Test
    void testGenerateTokenFromUsername() {
        String username = "test@example.com";
        String token = jwtTokenProvider.generateTokenFromUsername(username);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void testGetUsernameFromToken() {
        String username = "test@example.com";
        String token = jwtTokenProvider.generateTokenFromUsername(username);
        
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        
        assertEquals(username, extractedUsername);
    }
    
    @Test
    void testValidateToken() {
        String username = "test@example.com";
        String token = jwtTokenProvider.generateTokenFromUsername(username);
        
        assertTrue(jwtTokenProvider.validateToken(token));
    }
    
    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
    
    @Test
    void testGenerateRefreshToken() {
        String username = "test@example.com";
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);
        
        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.isRefreshToken(refreshToken));
    }
}
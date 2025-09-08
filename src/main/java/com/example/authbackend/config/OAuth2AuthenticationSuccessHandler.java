package com.example.authbackend.config;

import com.example.authbackend.security.JwtTokenProvider;
import com.example.authbackend.security.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Value("#{'${app.oauth2.authorized-redirect-uris}'.split(',')}")
    private List<String> authorizedRedirectUris;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        String targetUrl = determineTargetUrl(request, response, authentication);
        
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) {
        
        String targetUrl = getDefaultTargetUrl();
        
        String token = tokenProvider.generateTokenFromUsername(((UserPrincipal) authentication.getPrincipal()).getEmail());
        
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }
}
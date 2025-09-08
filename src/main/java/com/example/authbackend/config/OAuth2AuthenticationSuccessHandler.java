package com.example.authbackend.config;

import com.example.authbackend.security.JwtTokenProvider;
import com.example.authbackend.security.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtTokenProvider tokenProvider;
    
    // Define authorized redirect URIs directly (you can make this configurable later)
    private final List<String> authorizedRedirectUris = Arrays.asList(
        "http://localhost:3000/oauth2/redirect",
        "http://localhost:8080/oauth2/redirect"
    );
    
    // Constructor injection instead of @Autowired
    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    
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
        
        // Get the redirect URL from request parameter (optional)
        String redirectUri = request.getParameter("redirect_uri");
        
        // Use first authorized URI as default
        String targetUrl = authorizedRedirectUris.get(0);
        
        // Validate redirect URI if provided
        if (redirectUri != null && isAuthorizedRedirectUri(redirectUri)) {
            targetUrl = redirectUri;
        }
        
        // Generate JWT token
        String token = tokenProvider.generateTokenFromUsername(
            ((UserPrincipal) authentication.getPrincipal()).getEmail()
        );
        
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }
    
    private boolean isAuthorizedRedirectUri(String uri) {
        try {
            URI clientRedirectUri = URI.create(uri);
            
            return authorizedRedirectUris
                    .stream()
                    .anyMatch(authorizedRedirectUri -> {
                        URI authorizedURI = URI.create(authorizedRedirectUri);
                        return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                                && authorizedURI.getPort() == clientRedirectUri.getPort();
                    });
        } catch (Exception e) {
            return false;
        }
    }
}
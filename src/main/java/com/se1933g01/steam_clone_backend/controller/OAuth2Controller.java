package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/*
 * @author Loc Phan
 */

@RestController
@RequestMapping("/api/public/oauth2")
@CrossOrigin(origins = "*")
public class OAuth2Controller {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private AuthService authService;

    public record AuthResponse(String token, String email, String name) {}

    @GetMapping("/success")
    public ResponseEntity<?> oauth2Success(OAuth2AuthenticationToken authToken) {
        logger.info("Processing Google login at /api/public/oauth2/success");
        if (authToken == null) {
            logger.error("OAuth2AuthenticationToken is null. Check SecurityConfig OAuth2 setup and Google redirect.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Authentication token is missing. Ensure you are redirected from Google login.");
        }
        logger.info("AuthToken received: {}, Principal: {}", authToken.getName(), authToken.getPrincipal());
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null || email.isEmpty()) {
            logger.error("Email missing from Google profile");
            return ResponseEntity.badRequest().body("Email is missing");
        }
        if (name == null || name.isEmpty()) {
            logger.warn("Name missing, using default");
            name = "Unknown";
        }

        Boolean emailVerified = (Boolean) attributes.get("email_verified");
        if (emailVerified != null && !emailVerified) {
            logger.warn("Email not verified: {}", email);
            return ResponseEntity.badRequest().body("Email not verified by Google");
        }

        try {
            String jwt = authService.processOAuthPostLogin(email, name);
            logger.info("Google login successful: {}", email);
            return ResponseEntity.ok(new AuthResponse(jwt, email, name));
        } catch (Exception e) {
            logger.error("Google login failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/failure")
    public ResponseEntity<?> oauth2Failure() {
        logger.error("Google login failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google login failed");
    }
}
package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles OAuth2 login (Google).
 * Author: Loc Phan
 */
@RestController
@RequestMapping("/api/public/oauth2")
@CrossOrigin(origins = "*") // Allow frontend access
public class OAuth2Controller {

    @Autowired
    private AuthService authService;

    @GetMapping("/success")
    public ResponseEntity<?> oauth2Success(OAuth2AuthenticationToken authToken) {
        Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        //Check email_verified if needed
        Boolean emailVerified = (Boolean) attributes.get("email_verified");
        if (emailVerified != null && !emailVerified) {
            return ResponseEntity.badRequest().body("Email is not verified by Google.");
        }

        //Register or fetch the user, then return JWT
        String jwt = authService.processOAuthPostLogin(email, name);

        return ResponseEntity.ok(Map.of(
            "token", jwt,
            "email", email,
            "name", name
        ));
    }
}

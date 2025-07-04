package com.se1933g01.steamclonebackend.security;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.se1933g01.steamclonebackend.entity.user.Role;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author Loc Phan
 */

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // Value get from Application.Propertise
    @Value("${frontend.url}")
    private String frontendUrl;

    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(UserRepo userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        var oauthToken = (OAuth2AuthenticationToken) authentication;
        var attributes = oauthToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");

        // Check if user exists
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            String username = email.split("@")[0];
            user.setUsername(username);
            user.setPassword(""); // OAuth2 = no password
            user.setWalletBalance(BigDecimal.ZERO); // Changed by Phan Son 21-06
            user.setBanStatus(false);
            Role userRole = new Role();
            userRole.setRoleId(1L);
            user.setRole(userRole);
            userRepo.save(user);
        }

        System.out.println("Authenticated user: " + email);
        System.out.println("User found/created: " + user.getUsername());
        System.out.println("User role: " + user.getRole());
        System.out.println("User avatar: " + user.getAvatarUrl());
        String jwt = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getRole().getRoleName(),
                user.getAvatarUrl()); // Added by Phan Son 21-06

        // Redirect to frontend
        String redirectUrl = frontendUrl + "/oauth2/callback?token=" + jwt;
        response.sendRedirect(redirectUrl);
    }
}

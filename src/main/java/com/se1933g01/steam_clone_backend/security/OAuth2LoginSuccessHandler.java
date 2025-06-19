package com.se1933g01.steam_clone_backend.security;

import com.se1933g01.steam_clone_backend.entity.user.Role;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;
import com.se1933g01.steam_clone_backend.utils.JwtUtil;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Loc Phan
 */

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

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
            user.setUsername(email);
            user.setPassword(""); // OAuth2 = no password
            user.setWalletBalance(0.0);
            user.setBanStatus(false);
            Role userRole = new Role();
            userRole.setRoleId(1L);
            user.setRole(userRole);
            userRepo.save(user);
        }

        String jwt = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getRole().getRoleName());

        // Redirect to frontend
        String redirectUrl = "http://localhost:5173/?token=" + jwt;
        response.sendRedirect(redirectUrl);
    }
}

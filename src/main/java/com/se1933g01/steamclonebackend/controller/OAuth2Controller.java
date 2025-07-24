package com.se1933g01.steamclonebackend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.service.UserService;
import com.se1933g01.steamclonebackend.utils.JwtUtil;

@RestController
@RequestMapping("/api/public/oauth2")
public class OAuth2Controller {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String name = payload.get("name");

        // 1. Kiểm tra user đã có trong DB chưa, nếu chưa thì tạo
        User user = userService.findByEmail(email);
        if (user == null) {
            user = userService.createGoogleUser(email, name);
        }

        // 2. Tạo JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getRole().getRoleName(), user.getAvatarUrl(), user.isBanStatus());

        // 3. Trả token về frontend
        return ResponseEntity.ok(Map.of("token", token));
    }
}

/*
 * Copyright(C) 2025, Group1.
 * Steam CLone
 * Game Distribution System
 *
 * Record of change:
 * DATE            Version             AUTHOR           DESCRIPTION
 * 2025-06-05      1.0                 LocPG           Login and Register Controller
 */

package com.se1933g01.steamclonebackend.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.se1933g01.steamclonebackend.dto.LoginDTO;
import com.se1933g01.steamclonebackend.dto.RegisterRequestDTO;
import com.se1933g01.steamclonebackend.service.AuthService;
import com.se1933g01.steamclonebackend.service.EmailService;

/**
 * @author Loc Phan
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private EmailService emailService; 

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok("Registration successful!");
    }

    @GetMapping("/check-email")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean emailExists = authService.emailExists(email);
        return ResponseEntity.ok().body(Map.of("available", !emailExists));
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        return authService.login(request);
    }
}

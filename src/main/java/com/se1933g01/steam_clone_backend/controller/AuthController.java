/*
 * Copyright(C) 2025, Group1.
 * Steam CLone
 * Game Distribution System
 *
 * Record of change:
 * DATE            Version             AUTHOR           DESCRIPTION
 * 2025-06-05      1.0                 LocPG           Login and Register Controller
 */

package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.LoginDTO;
import com.se1933g01.steam_clone_backend.dto.RegisterRequestDTO;
import com.se1933g01.steam_clone_backend.service.AuthService;
import com.se1933g01.steam_clone_backend.service.EmailService;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // allow frontend access
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private EmailService emailService; 

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok("Registration successful!");
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean emailExists = authService.emailExists(email);
        return ResponseEntity.ok().body(Map.of("available", !emailExists));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        return authService.login(request);
    }
}

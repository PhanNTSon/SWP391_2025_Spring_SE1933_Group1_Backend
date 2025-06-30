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
import com.se1933g01.steam_clone_backend.service.EmailVerificationService;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Loc Phan
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private EmailVerificationService emailverificationService;

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

    @GetMapping("/check-username")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean usernameExists = authService.usernameExists(username);
        return ResponseEntity.ok().body(Map.of("available", !usernameExists));
    }

    @PostMapping("/send-verification-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        emailverificationService.sendOtpToEmail(email);
        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String otp = req.get("otp");

        boolean valid = emailverificationService.verifyOtp(email, otp);
        if (valid) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }

}

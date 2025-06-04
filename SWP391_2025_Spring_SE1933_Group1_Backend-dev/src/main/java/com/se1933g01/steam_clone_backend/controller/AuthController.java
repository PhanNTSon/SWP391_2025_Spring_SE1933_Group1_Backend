package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.RegisterRequestDTO;
import com.se1933g01.steam_clone_backend.service.AuthService;
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
}

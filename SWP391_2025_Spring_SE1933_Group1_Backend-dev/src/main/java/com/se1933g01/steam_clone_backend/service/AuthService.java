package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.RegisterRequestDTO;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> register(RegisterRequestDTO request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setWalletBalance(0.0); // default balance
        user.setBanStatus(false); // default status

        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }


    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }
}

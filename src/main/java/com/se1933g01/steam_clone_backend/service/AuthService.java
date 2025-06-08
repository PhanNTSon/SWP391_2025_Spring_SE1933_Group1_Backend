package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.LoginDTO;
import com.se1933g01.steam_clone_backend.dto.RegisterRequestDTO;
import com.se1933g01.steam_clone_backend.entity.user.Role;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.RoleRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;
    // @Autowired
    // private RoleRepo roleRepo;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> register(RegisterRequestDTO request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setCountry(request.getCountry());
        user.setWalletBalance(0.0); // default balance
        user.setBanStatus(false); // default status

        Role userRole = new Role();
        userRole.setRoleId(1L);
        user.setRole(userRole);

        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> login(LoginDTO request) {
        Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = optionalUser.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok("Login successful");
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }
}

package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.LoginDTO;
import com.se1933g01.steam_clone_backend.dto.RegisterRequestDTO;
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.entity.user.Role;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;
import com.se1933g01.steam_clone_backend.utils.JwtUtil;

import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Loc
 */
@Service
public class AuthService {

    /**
     * @author Phan NT Son
     */
    
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    AuthService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private final UserRepo userRepo;

    @Transactional // Added by Phan NT Son
    public ResponseEntity<String> register(RegisterRequestDTO request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        // Added and fixed by Phan NT Son
        String hashed = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashed);

        user.setCountry(request.getCountry());
        user.setWalletBalance(0.0); // default balance
        user.setBanStatus(false); // default status

        Role userRole = new Role();
        userRole.setRoleId(1L);
        user.setRole(userRole);

        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // public ResponseEntity<?> login(LoginDTO request) {
    // Optional<User> optionalUser = userRepo.findByUsername(request.getUsername());

    // if (optionalUser.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    // }

    // User user = optionalUser.get();
    // if (!user.getPassword().equals(request.getPassword())) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid
    // password");
    // }

    // return ResponseEntity.ok("Login successful");
    // }

    /**
     * @author Phan NT Son
     * @param request
     * @return
     */
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        try {
            // Dùng AuthenticationManager để xác thực username và password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Lấy thông tin người dùng sau khi xác thực thành công
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();

            // Tạo JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUser().getUserID(),
                    userDetails.getUser().getRole().getRoleName());

            // Trả về cho client
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    /**
     * @author Loc Phan
     * @param email
     * @param name
     * @return JWT token
     */
    public String processOAuthPostLogin(String email, String name) {
        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // tạo username bằng email
            user.setPassword(""); // rỗng vì OAuth2 không sử dụng mật khẩu
            user.setWalletBalance(0.0);
            user.setBanStatus(false); // default status

            Role userRole = new Role();
            userRole.setRoleId(1L); // Default user role
            user.setRole(userRole);

            userRepo.save(user);
        }

        // Generate JWT
        return jwtUtil.generateToken(user.getUsername(), user.getUserID(), user.getRole().getRoleName()); // Adjust by
                                                                                                          // Phan NT SOn
                                                                                                          // 18-06-2025
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }
}

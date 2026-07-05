package com.se1933g01.steamclonebackend.service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.se1933g01.steamclonebackend.dto.LoginDTO;
import com.se1933g01.steamclonebackend.dto.RegisterRequestDTO;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.entity.user.Role;
import com.se1933g01.steamclonebackend.entity.user.SessionLog;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.SessionLogRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.utils.JwtUtil;

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

    private final UserRepo userRepo;
    private final SessionLogRepo sessionLogRepo;
    public AuthService(UserRepo userRepo, SessionLogRepo sessionLogRepo) {
        this.userRepo = userRepo;
        this.sessionLogRepo = sessionLogRepo;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Transactional // Added by Phan NT Son
    public ResponseEntity<String> register(RegisterRequestDTO request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        // Added and fixed by Phan NT Son
        String hashed = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashed);

        user.setCountry(request.getCountry());
        user.setWalletBalance(BigDecimal.ZERO); // Changed by Pha Son 21-06
        user.setBanStatus(false); // default status
        user.setAvatarUrl("https://avatars.steamstatic.com/b5bd56c1aa4644a474a2e4972be27ef9e82e517e_full.jpg");
        // ↑ Added by Phan Son 28-06

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
            //save to sessionlog
            SessionLog log = new SessionLog();
            log.setUser(userDetails.getUser());  // assuming this returns a User entity
            log.setLoginTime(LocalDateTime.now());
            sessionLogRepo.save(log);
            // Tạo JWT token
            String token = jwtUtil.generateToken(
                    userDetails.getUsername(),
                    userDetails.getUser().getUserId(),
                    userDetails.getUser().getRole().getRoleName(),
                    userDetails.getUser().getAvatarUrl(),
                    userDetails.getUser().isBanStatus()); // Added by
                                                          // Phan Son
                                                          // 21-06

            // Trả về cho client
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + ex.getMessage());
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
            user.setWalletBalance(BigDecimal.ZERO); // Changed by Pha Son 21-06
            user.setBanStatus(false); // default status

            Role userRole = new Role();
            userRole.setRoleId(1L); // Default user role
            user.setRole(userRole);

            userRepo.save(user);
        }

        // Generate JWT
        return jwtUtil.generateToken(user.getUsername(),
                user.getUserId(),
                user.getRole().getRoleName(),
                user.getAvatarUrl(),
                user.isBanStatus()); // Added by Phan Son 21-06
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    /**
     * @author Loc Phan
     * @param username
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepo.existsByUsername(username);
    }

}

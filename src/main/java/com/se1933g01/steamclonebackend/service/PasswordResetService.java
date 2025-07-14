package com.se1933g01.steamclonebackend.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.entity.user.PasswordResetToken;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.PasswordResetTokenRepository;
import com.se1933g01.steamclonebackend.repository.UserRepo;


/**
 * @author Loc Phan
 */

@Service
public class PasswordResetService {

    private final UserRepo userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepo userRepository, 
                                PasswordResetTokenRepository tokenRepository, 
                                EmailService emailService, 
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void requestPasswordReset(String username, String email) {
        User user = userRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = emailService.generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        tokenRepository.deleteByUserID(user.getUserId()); // clear previous

        PasswordResetToken token = PasswordResetToken.builder()
                .userID(user.getUserId())
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiryTime(expiry)
                .build();

        tokenRepository.save(token);

        emailService.sendOtpEmail(user.getEmail(), otp, "Your Password Reset OTP");
    }

    @Transactional
    public void resetPassword(String username, String otp, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken token = tokenRepository.findByUserIDAndOtp(user.getUserId(), otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP."));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.deleteByUserID(user.getUserId()); // cleanup
    }

    
}

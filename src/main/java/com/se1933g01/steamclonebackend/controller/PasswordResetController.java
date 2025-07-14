package com.se1933g01.steamclonebackend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.service.PasswordResetService;


/**
 * @author Loc Phan
 */

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");

        passwordResetService.requestPasswordReset(username, email);
        return ResponseEntity.ok("OTP sent.");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String otp = req.get("otp");
        String newPassword = req.get("newPassword");
        String confirmPassword = req.get("confirmPassword");

        passwordResetService.resetPassword(username, otp, newPassword, confirmPassword);
        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/change/request")
    public ResponseEntity<?> requestChange(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");

        passwordResetService.requestPasswordReset(username, email);
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/change/confirm")
    public ResponseEntity<?> confirmChange(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String otp = req.get("otp");
        String newPassword = req.get("newPassword");
        String confirmPassword = req.get("confirmPassword");

        passwordResetService.resetPassword(username, otp, newPassword, confirmPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }

}

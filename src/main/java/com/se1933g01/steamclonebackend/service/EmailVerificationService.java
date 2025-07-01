package com.se1933g01.steamclonebackend.service;

import com.se1933g01.steamclonebackend.entity.user.EmailVerificationToken;
import com.se1933g01.steamclonebackend.repository.EmailVerificationTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepo tokenRepo;
    private final EmailService emailService;

    @Transactional
    public void sendOtpToEmail(String email) {
        String otp = emailService.generateOtp();

        // Delete old OTP if exists
        tokenRepo.deleteByEmail(email);

        // Save new OTP
        EmailVerificationToken token = EmailVerificationToken.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .build();

        tokenRepo.save(token);

        emailService.sendOtpEmail(email, otp, "Your SteamCL Verification Code");
    }

    @Transactional
    public boolean verifyOtp(String email, String inputOtp) {
        return tokenRepo.findByEmail(email)
                .filter(t -> t.getExpiryTime().isAfter(LocalDateTime.now()))
                .map(t -> t.getOtp().equals(inputOtp))
                .orElse(false);
    }
}

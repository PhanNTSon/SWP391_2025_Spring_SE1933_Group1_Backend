package com.se1933g01.steam_clone_backend.repository;

import com.se1933g01.steam_clone_backend.entity.user.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByEmailAndOtp(String email, String otp);
    void deleteByEmail(String email);
    Optional<EmailVerificationToken> findByEmail(String email);
}
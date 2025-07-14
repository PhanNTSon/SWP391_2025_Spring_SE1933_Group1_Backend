package com.se1933g01.steamclonebackend.repository;

import com.se1933g01.steamclonebackend.entity.user.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUserIDAndOtp(Long userId, String otp);
    void deleteByUserID(Long userId);
}


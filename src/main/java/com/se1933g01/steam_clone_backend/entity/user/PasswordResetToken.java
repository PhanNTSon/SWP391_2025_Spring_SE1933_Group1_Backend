package com.se1933g01.steam_clone_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author: Loc Phan
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenID;

    private Long userID;
    private String otp;
    private LocalDateTime expiryTime;
    private LocalDateTime createdAt;
}



package com.se1933g01.steamclonebackend.entity.user;

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
@Table(name = "PasswordResetToken")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenID")
    private Long tokenID;

    @Column(name = "UserID")
    private Long userID;
    
    @Column(name = "OTP")
    private String otp;

    @Column(name = "ExpiryTime")
    private LocalDateTime expiryTime;

    @Column(name = "CreateAt")
    private LocalDateTime createdAt;
}

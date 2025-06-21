package com.se1933g01.steam_clone_backend.dto.User;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author kerri
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {

    private Long userId;

    private String email;

    private String username;

    private BigDecimal walletBalance; // Changed by Phan Son 21-06

    private String avatarUrl;

    private String country;

    private LocalDate dob; // Changed by Phan Son 21-06

    private Character gender;

    private String profileName;

    private String summary;

    private boolean banStatus;
}

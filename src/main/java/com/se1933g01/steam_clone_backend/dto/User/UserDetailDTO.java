package com.se1933g01.steam_clone_backend.dto.User;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

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

    private Double walletBalance;

    private String avatarUrl;

    private String country;

    private Date dob;

    private Character gender;

    private String profileName;

    private String summary;

    private boolean banStatus;
}

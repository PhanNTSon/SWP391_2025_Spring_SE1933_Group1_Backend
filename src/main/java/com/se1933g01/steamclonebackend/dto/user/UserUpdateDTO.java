package com.se1933g01.steamclonebackend.dto.user;


import java.time.LocalDate;

import lombok.Data;

/**
 * @author kerri
 */
@Data
public class UserUpdateDTO {
    // annotation validation
    private String profileName;
    private String email;
    private String avatarUrl;
    private String country;
    private LocalDate dob; // Changed by Phan NT Son 21-06
    private Character gender;
    private String summary;
}
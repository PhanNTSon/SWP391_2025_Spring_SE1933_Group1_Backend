package com.se1933g01.steam_clone_backend.dto.User;

import java.time.LocalDate;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kerri
 */
@Getter
@Setter
public class UserUpdateDTO {
    // annotation validation
    private String profileName;
    private String email;
    private String country;
    private Date dob;
    private Character gender;
    private String summary;
}
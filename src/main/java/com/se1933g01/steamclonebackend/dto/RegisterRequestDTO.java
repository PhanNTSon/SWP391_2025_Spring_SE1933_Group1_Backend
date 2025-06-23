package com.se1933g01.steamclonebackend.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String email;
    private String country;
    private String username;
    private String password;
    private String confirmPassword;

    // Changed by Phan Son 21-06
}

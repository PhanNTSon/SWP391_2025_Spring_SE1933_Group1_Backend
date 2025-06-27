package com.se1933g01.steamclonebackend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailChangeConfirmDTO {
    private String newEmail;
    private String token;
}

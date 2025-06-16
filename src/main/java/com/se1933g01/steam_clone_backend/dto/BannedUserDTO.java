package com.se1933g01.steam_clone_backend.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Phan NT Son
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannedUserDTO {
    private long userID;
    private String username;
    private String roleName;
}

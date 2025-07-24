package com.se1933g01.steamclonebackend.dto;


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

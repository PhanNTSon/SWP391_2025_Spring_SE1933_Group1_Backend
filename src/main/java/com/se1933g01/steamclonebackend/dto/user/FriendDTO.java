package com.se1933g01.steamclonebackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    private Long friendId;
    private String friendName;
    private String friendAvatarUrl;
    private Integer groupsTakeIn;
}

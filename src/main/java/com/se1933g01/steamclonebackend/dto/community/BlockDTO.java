package com.se1933g01.steamclonebackend.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockDTO {
    private Long blockerId;
    private String blockerName;
    private String blockerAvatar;

    private Long blockedId;
    private String blockedName;
    private String blockedAvatar;

}

package com.se1933g01.steamclonebackend.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteDTO {
    private long invitorId;
    private String invitorName;
}

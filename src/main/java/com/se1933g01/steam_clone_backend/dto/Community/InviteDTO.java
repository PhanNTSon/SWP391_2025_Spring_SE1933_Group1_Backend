package com.se1933g01.steam_clone_backend.dto.Community;

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

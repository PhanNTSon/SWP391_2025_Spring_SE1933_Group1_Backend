package com.se1933g01.steamclonebackend.dto.community;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDTO {
    private long userId;
    private long friendId;
    private String status;
    private LocalDate createdAt;
}

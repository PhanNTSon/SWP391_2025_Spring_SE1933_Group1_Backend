package com.se1933g01.steam_clone_backend.dto.Community;

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

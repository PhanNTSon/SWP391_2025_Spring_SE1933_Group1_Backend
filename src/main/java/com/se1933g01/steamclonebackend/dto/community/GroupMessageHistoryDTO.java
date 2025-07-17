package com.se1933g01.steamclonebackend.dto.community;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageHistoryDTO {
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private LocalDateTime createdAt;
}

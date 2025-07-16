package com.se1933g01.steamclonebackend.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDTO {
    private Long senderId;
    private Long receiverId;

    private String senderName;
    private String receiverName;

    private String senderAvatar;
    private String receiverAvatar;
}

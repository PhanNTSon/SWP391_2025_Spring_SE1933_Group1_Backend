package com.se1933g01.steamclonebackend.dto.community;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChatMessageDTO {
    private Long conversationId;
    private Long senderId;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime sentAt;
}

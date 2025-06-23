package com.se1933g01.steam_clone_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private long conversationId;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime sentAt;
}

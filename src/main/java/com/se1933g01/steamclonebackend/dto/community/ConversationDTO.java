package com.se1933g01.steamclonebackend.dto.community;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private long conversationId;
    private List<MessageDTO> messages;
}

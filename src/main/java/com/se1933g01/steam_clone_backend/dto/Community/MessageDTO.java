package com.se1933g01.steam_clone_backend.dto.Community;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String messageContent;
    private LocalDateTime createAt;
}

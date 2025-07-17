package com.se1933g01.steamclonebackend.dto.community;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionThreadDTO {
    private Long threadId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String username;
}


package com.se1933g01.steam_clone_backend.dto;

import lombok.Data;

@Data
public class FeedbackDTO {
    private long requestId;
    private String subject;
    private String message;
}

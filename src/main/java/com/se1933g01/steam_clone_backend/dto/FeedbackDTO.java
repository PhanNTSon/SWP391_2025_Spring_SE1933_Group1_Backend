package com.se1933g01.steam_clone_backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class FeedbackDTO {
    private long requestId;
    private String subject;
    private String message;
    private List<String> mediaUrls;
    private String userName;
    private long userId;
}

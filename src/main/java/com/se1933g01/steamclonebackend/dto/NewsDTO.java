package com.se1933g01.steamclonebackend.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
@Data
public class NewsDTO {
    private Long newsId;
    private String title;
    private String summary;
    private String htmlContent;
    private LocalDate createdAt;
    private String thumbnail;
    private String markdown;
    // getters/setters
}
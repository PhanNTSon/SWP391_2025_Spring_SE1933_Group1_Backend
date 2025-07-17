package com.se1933g01.steamclonebackend.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class AddingGameRequestDTO {
    private Long requestId;
    private Long gameId;
    private String gameName;
    private BigDecimal price; // Changed by Phan Son 21-06
    private String shortDescription;
    private String fullDescription;
    private String os;
    private String processor;
    private String memory;
    private String graphics;
    private String storage;
    private String additionalNotes;
    private String gameUrl;
    private String iconUrl;
    private List<String> mediaUrls;
    private String publisherName;
    private String publisherId;
    private String sendDate;
    private List<Integer> tags;
}

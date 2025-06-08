package com.se1933g01.steam_clone_backend.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AddingGameRequestDTO {
    private Long requestId;
    private String gameName;
    private double price;
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
}

package com.se1933g01.steamclonebackend.dto;

import lombok.Data;

@Data
public class PublisherApplyRequestDTO {
    private Long requestId;
    String legalName;
    String publisherName;
    String address;
    String socialNumber;
    String country;
    String imageUrl;
    String userId;
}

package com.se1933g01.steam_clone_backend.dto;

import lombok.Data;

@Data
public class PublisherApplyRequestDTO {
    String legalName;
    String publisherName;
    String address;
    String socialNumber;
    String country;
    String imageUrl;
}

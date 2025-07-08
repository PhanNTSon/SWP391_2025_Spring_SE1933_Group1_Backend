package com.se1933g01.steamclonebackend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublisherBasicDTO {
    private Long publisherId;
    private String publisherName;
    private String imageUrl;
}

package com.se1933g01.steam_clone_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaDTO {
    private long mediaId;
    private String url;
    private String type;
}
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
    public MediaDTO(long mediaId2, String url2, String type2) {
        //TODO Auto-generated constructor stub
    }
    private Integer mediaId;
    private String url;
    private String type;
}
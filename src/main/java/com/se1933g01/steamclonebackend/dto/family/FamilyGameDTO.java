package com.se1933g01.steamclonebackend.dto.family;

import java.util.List;

import com.se1933g01.steamclonebackend.dto.MediaDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyGameDTO {
    private Long id;
    private String name;
    private String gameUrl;
    private Boolean isPlayable;
    private List<MediaDTO> media;
}

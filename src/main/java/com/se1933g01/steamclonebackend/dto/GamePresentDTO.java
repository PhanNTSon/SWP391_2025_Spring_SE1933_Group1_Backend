package com.se1933g01.steamclonebackend.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamePresentDTO {
    private Long id;
    private String name;
    private List<MediaDTO> medias; // Danh sách DTO cho media
    private String shortDescription;
    private BigDecimal price;
    private List<TagDTO> tags; // Danh sách DTO cho tags

}

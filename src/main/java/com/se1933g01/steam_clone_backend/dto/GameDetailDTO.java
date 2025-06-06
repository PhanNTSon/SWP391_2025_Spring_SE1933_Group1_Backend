package com.se1933g01.steam_clone_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Date; // Hoặc LocalDate
import java.util.Set;
import java.util.List; // Hoặc Set tùy ý

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDTO {
    private Integer gameId;
    private PublisherBasicDTO publisher; // Sử dụng DTO cho publisher
    private String name;
    private Date releaseDate;
    private Boolean state;
    private BigDecimal price;
    private String shortDescription;
    private String fullDescription;
    private Integer totalPurchased;
    private Set<TagDTO> tags; // Danh sách DTO cho tags
    // private SystemRequirementDTO systemRequirement; // DTO cho systemRequirement
    private List<MediaDTO> media; // Danh sách DTO cho media
    private String os;
    private String storage;
    private String processor;
    private String memory;
    private String additionalNotes;
    private String graphics;
}

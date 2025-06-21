package com.se1933g01.steam_clone_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameBasicDTO {
    private long id; // Tương ứng với gameId
    private String title; // Tương ứng với name
    private String imageUrl; // Sẽ lấy từ Media entity
    private BigDecimal price; // Changed by Phan Son 21-06
    private BigDecimal discountPrice; // Changed by Phan Son 21-06
    private BigDecimal originalPrice; // Changed by Phan Son 21-06

    // Added by Phan NT Son 17-06-2025
    private LocalDate releaseDate;

    public GameBasicDTO(long gameId, String gameName, BigDecimal price) { // Changed by Phan Son 21-06
        this.id = gameId;
        this.title = gameName;
        this.price = price;
    }
}
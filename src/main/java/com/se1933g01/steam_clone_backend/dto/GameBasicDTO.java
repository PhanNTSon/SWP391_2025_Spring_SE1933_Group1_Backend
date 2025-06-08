package com.se1933g01.steam_clone_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameBasicDTO {
    private long id; // Tương ứng với gameId
    private String title; // Tương ứng với name
    private String imageUrl; // Sẽ lấy từ Media entity
    private double price; // Giá bán hiệu lực (sau khi đã áp dụng giảm giá nếu có)
    private double discountPrice; // Giá sau khi giảm giá (nếu không có giảm giá, có thể bằng originalPrice)
    private double originalPrice; // Giá gốc của sản phẩm (từ game.price)

    public GameBasicDTO(long gameId, String gameName){
        this.id = gameId;
        this.title = gameName;
    }
}
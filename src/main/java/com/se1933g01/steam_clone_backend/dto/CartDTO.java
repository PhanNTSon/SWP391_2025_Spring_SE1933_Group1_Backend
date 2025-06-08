package com.se1933g01.steam_clone_backend.dto;

import lombok.Data;
import java.util.Set;
import java.util.HashSet;

@Data
public class CartDTO {
    private Long userId;
    private Set<CartItemDTO> cartItems = new HashSet<>();
    private Double total;

    @lombok.Data
    public static class CartItemDTO {
        private Long gameId;
        private String gameName;
        private Double price;
    }
}

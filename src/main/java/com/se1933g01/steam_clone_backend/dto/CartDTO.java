package com.se1933g01.steam_clone_backend.dto;

import lombok.Data;
import java.util.Set;
import java.util.HashSet;

@Data
public class CartDTO {
    private Long userId;
    private Long gameId;
    private String gameName;
    private Double price;
    private Double total;
}

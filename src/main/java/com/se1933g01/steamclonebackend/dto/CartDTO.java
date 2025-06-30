package com.se1933g01.steamclonebackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private Long userId;
    private List<GameBasicDTO> listCart;
}

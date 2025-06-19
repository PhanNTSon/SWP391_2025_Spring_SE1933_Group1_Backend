package com.se1933g01.steam_clone_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class LibraryDTO {
    private Long userId;
    private List<GameDetailDTO> library;
}

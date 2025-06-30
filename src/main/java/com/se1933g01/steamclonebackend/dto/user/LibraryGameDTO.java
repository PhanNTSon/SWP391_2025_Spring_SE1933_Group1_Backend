package com.se1933g01.steamclonebackend.dto.user;

import java.time.LocalDateTime;

import com.se1933g01.steamclonebackend.dto.GameDetailDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryGameDTO {
    private GameDetailDTO gameDetail;
    // Thông tin dành riêng cho user trong thư viện
    private LocalDateTime dateAdded;
    private Long playtimeInMillis;

}

package com.se1933g01.steamclonebackend.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO chính để đại diện cho toàn bộ thư viện của một người dùng.
 */
@Data // Tự động tạo getters, setters, toString, equals, hashCode
public class LibraryDTO {
    private Long userId;
    private int gameCount;
    private List<LibraryGameDTO> games;
}

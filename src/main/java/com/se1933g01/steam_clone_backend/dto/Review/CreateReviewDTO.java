package com.se1933g01.steam_clone_backend.dto.Review;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    private Long gameId;
    private Long userId;
    private String userName;
    private String reviewContent;
    private boolean isRecommended;
}

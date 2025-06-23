package com.se1933g01.steamclonebackend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    private long gameId;
    private boolean recommended;
    private String reviewContent;
    
}

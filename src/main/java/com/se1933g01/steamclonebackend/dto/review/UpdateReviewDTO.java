package com.se1933g01.steamclonebackend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewDTO {
    private String reviewContent;
    private boolean recommended;
}

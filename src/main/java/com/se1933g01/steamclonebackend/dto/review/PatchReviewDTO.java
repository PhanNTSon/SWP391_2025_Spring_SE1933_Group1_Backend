package com.se1933g01.steamclonebackend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchReviewDTO {
    private long gameId;
    private long authorId;
}

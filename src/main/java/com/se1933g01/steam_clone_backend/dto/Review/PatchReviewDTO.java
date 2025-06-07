package com.se1933g01.steam_clone_backend.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchReviewDTO {
    private long userId;
    private long helpful;
    private long notHelpful;
}

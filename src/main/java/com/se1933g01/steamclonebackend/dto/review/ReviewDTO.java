package com.se1933g01.steamclonebackend.dto.review;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private String userName;
    private long userId;
    private String reviewContent;
    private boolean recommended;
    private long helpful;
    private long notHelpful;
    private LocalDate timeCreated;

}

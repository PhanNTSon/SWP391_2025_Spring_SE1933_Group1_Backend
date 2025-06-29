package com.se1933g01.steamclonebackend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * @author Phan NT Son
 *         Type: UPDATE_REACTION, UPDATE_CONTENT, NEW_REVIEW, DELETE_REVIEW
 */
public class ReviewSocketDTO {
    private String type;
    private long authorId;
    private long gameId;
    private boolean newRecommended;
    private String newContent;
    private int newLikeCount;
    private int newUnLikeCount;
}

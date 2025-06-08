package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.Review.CreateReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.ReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.UpdateReviewDTO;
import com.se1933g01.steam_clone_backend.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Author: Phan Son
 */
@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Create new Review
     * 
     * @param dto
     * @param gameId
     * @return
     */
    @PostMapping("/{gameId}/post-review")
    public ResponseEntity<CreateReviewDTO> createReview(@RequestBody CreateReviewDTO dto,
            @PathVariable("gameId") Long gameId) {
        CreateReviewDTO result = reviewService.createReview(dto.getUserId(), gameId, dto.getReviewContent(),
                dto.isRecommended());
        return ResponseEntity.ok(result);
    }

    /**
     * Get Review list of a Game.
     * 
     * @param gameId
     * @return
     */
    @GetMapping("/{gameId}/review-list")
    public ResponseEntity<List<ReviewDTO>> getReviewList(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(reviewService.getGameReviewList(gameId));
    }

    /**
     * 
     * @param dto
     * @param gameId
     * @return
     */
    @PutMapping("/{gameId}/update-review")
    public ResponseEntity<UpdateReviewDTO> updateReview(@RequestBody UpdateReviewDTO dto,
            @PathVariable("gameId") Long gameId) {
        System.out.println("Here: " + dto.isRecommended());
        UpdateReviewDTO result = reviewService.updateReview(gameId, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * 
     * @param userId
     * @param gameId
     * @return
     */
    @DeleteMapping("/{gameId}/delete-review/{userId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("userId") Long userId,
            @PathVariable("gameId") Long gameId) {
        reviewService.deleteReview(gameId, userId);
        return ResponseEntity.noContent().build();
    }

}

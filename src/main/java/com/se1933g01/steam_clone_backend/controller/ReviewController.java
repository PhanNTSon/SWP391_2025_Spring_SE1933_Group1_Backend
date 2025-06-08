package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.Review.CreateReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.PatchReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.ReviewDTO;
import com.se1933g01.steam_clone_backend.repository.ReviewRepo;
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
     * Update Review's helpful/ not helpful count.
     * 
     * @param dto
     * @param gameId
     * @return
     */
    @PutMapping("/{gameId}/put-review")
    public ResponseEntity<PatchReviewDTO> putReview(@RequestBody PatchReviewDTO dto,
            @PathVariable("gameId") Long gameId) {
        PatchReviewDTO result = reviewService.putReview(gameId, dto.getUserId(), dto.getHelpful(),
                dto.getNotHelpful());
        return ResponseEntity.ok(result);
    }

    // @PutMapping("/{gameId}/update-review")
    // public ResponseEntity<PatchReviewDTO> updateReview(@RequestBody
    // PatchReviewDTO dto,
    // @PathVariable("gameId") Long gameId) {
    // PatchReviewDTO result = reviewService.putReview(gameId, dto.getUserId(),
    // dto.getHelpful(),
    // dto.getNotHelpful());
    // return ResponseEntity.ok(result);
    // }

    
}

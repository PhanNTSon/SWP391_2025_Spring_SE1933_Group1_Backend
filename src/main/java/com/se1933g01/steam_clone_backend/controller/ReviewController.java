package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{gameId}/{authorId}/check")
    public ResponseEntity<Integer> getUserReaction(@RequestParam Long userId,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {

        int result = reviewService.checkUserReaction(userId, reviewGameId, reviewAuthorId);
        return ResponseEntity.ok(result);
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
        UpdateReviewDTO result = reviewService.updateReview(gameId, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Update Review helpful count
     * 
     * @param userId
     * @param reviewGameId
     * @param reviewAuthorId
     * @return
     */
    @PatchMapping("/{gameId}/{authorId}/helpful")
    public ResponseEntity<String> likeReview(@RequestBody UpdateReviewDTO dto,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            reviewService.patchLikeReview(dto.getUserId(), reviewGameId, reviewAuthorId);
            return ResponseEntity.ok("Done helpful");
        } catch (Exception e) {
            return ResponseEntity.ok("Not done helpful");

        }
    }

    /**
     * Update Review Not helpful count
     * 
     * @param userId
     * @param reviewGameId
     * @param reviewAuthorId
     * @return
     */
    @PatchMapping("/{gameId}/{authorId}/not-helpful")
    public ResponseEntity<String> dislikeReview(@RequestBody UpdateReviewDTO dto,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            reviewService.patchUnLikeReview(dto.getUserId(), reviewGameId, reviewAuthorId);
            return ResponseEntity.ok("Done not helpful");
        } catch (Exception e) {
            return ResponseEntity.ok("Not Done not helpful");

        }
    }

    @PatchMapping("/{gameId}/{authorId}/clean-reaction")
    public ResponseEntity<String> reactionReview(@RequestBody UpdateReviewDTO dto,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            reviewService.deleteReactionReview(dto.getUserId(), reviewGameId, reviewAuthorId);
            return ResponseEntity.ok("Done clean");
        } catch (Exception e) {
            return ResponseEntity.ok("Not Done clean");

        }
    }

    /**
     * Delete Review
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

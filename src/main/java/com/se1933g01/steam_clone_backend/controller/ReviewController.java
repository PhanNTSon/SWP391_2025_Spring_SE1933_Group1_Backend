package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.service.ReviewService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Author: Phan Son
 */
@RestController
public class ReviewController {

    private final ReviewService reviewService;

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
    @PostMapping("/user/review/post")
    public ResponseEntity<CreateReviewDTO> createReview(@RequestBody CreateReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        CreateReviewDTO result = reviewService.createReview(me.getUser().getUserID(), dto.getGameId(),
                dto.getReviewContent(),
                dto.isRecommended());
        return ResponseEntity.ok(result);
    }

    /**
     * Get Review list of a Game.
     * 
     * @param gameId
     * @return
     */
    @GetMapping("/game/{gameId}/reviews/{page}")
    public ResponseEntity<List<ReviewDTO>> getReviewList(@PathVariable("gameId") Long gameId,
            @PathVariable(name = "page") int pageNum) {
        return ResponseEntity.ok(reviewService.getGameReviewList(gameId));
    }

    @GetMapping("/{gameId}/{authorId}/check")
    public ResponseEntity<?> getUserReaction(@RequestParam Long userId,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            int result = reviewService.checkUserReaction(userId, reviewGameId, reviewAuthorId);
            return ResponseEntity.ok(result);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
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
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
        }
    }

    @PatchMapping("/{gameId}/{authorId}/clean-reaction")
    public ResponseEntity<String> reactionReview(@RequestBody UpdateReviewDTO dto,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            reviewService.deleteReactionReview(dto.getUserId(), reviewGameId, reviewAuthorId);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");

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
        reviewService.deleteReview(userId, gameId);
        return ResponseEntity.noContent().build();
    }

}

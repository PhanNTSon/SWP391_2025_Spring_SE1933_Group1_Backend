package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final String responseStringOk = "SUCCESS";
    private static final String responseStringError = "SERVER ERROR";

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
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<CreateReviewDTO> createReview(@RequestBody CreateReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        CreateReviewDTO result = reviewService.createReview(me.getUser().getUserId(), dto.getGameId(),
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
    @GetMapping("/list/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewList(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(reviewService.getGameReviewList(gameId));
    }

    @GetMapping("/{gameId}/{authorId}/check")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> getUserReaction(@RequestParam Long userId,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            int result = reviewService.checkUserReaction(userId, reviewGameId, reviewAuthorId);
            return ResponseEntity.ok(String.valueOf(result));

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
    @PutMapping("/update/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
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
    @PatchMapping("/vote/helpful")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> likeReview(@RequestBody UpdateReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        try {
            reviewService.patchLikeReview(me.getUser().getUserId(), dto.getGameId(), dto.getAuthorId());
            return ResponseEntity.ok().body(responseStringOk);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseStringError);
        }
    }

    /**
     * Update Review Not helpful count
     * 
     * @param dto include ReviewGameID & ReviewAuthorID
     * @return
     */
    @PatchMapping("/vote/unhelpful")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> dislikeReview(@RequestBody UpdateReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        try {
            reviewService.patchUnLikeReview(me.getUser().getUserId(), dto.getGameId(), dto.getAuthorId());
            return ResponseEntity.ok().body(responseStringOk);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseStringError);
        }
    }

    /**
     * 
     * @param dto include ReviewGameID & ReviewAuthorID
     * @return
     */
    @PatchMapping("/vote/clean")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> reactionReview(@RequestBody UpdateReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        try {
            reviewService.deleteReactionReview(me.getUser().getUserId(), dto.getGameId(), dto.getAuthorId());
            return ResponseEntity.ok().body(responseStringOk);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseStringError);
        }
    }

    /**
     * Delete Review
     * 
     * @param userId
     * @return
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal CustomUserDetail me,
            @RequestBody Long gameId) {
        reviewService.deleteReview(me.getUser().getUserId(), gameId);
        return ResponseEntity.noContent().build();
    }

}

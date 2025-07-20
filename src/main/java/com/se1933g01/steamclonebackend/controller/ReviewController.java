package com.se1933g01.steamclonebackend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.ApiRespDTO;
import com.se1933g01.steamclonebackend.dto.review.CreateReviewDTO;
import com.se1933g01.steamclonebackend.dto.review.PatchReviewDTO;
import com.se1933g01.steamclonebackend.dto.review.ReviewDTO;
import com.se1933g01.steamclonebackend.dto.review.UpdateReviewDTO;
import com.se1933g01.steamclonebackend.entity.game.Review;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.service.ReviewService;
import com.se1933g01.steamclonebackend.utils.GeminiContentModerator;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author: Phan Son
 */
@RestController
@RequestMapping("/review")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private static final String RESPONSE_STRING_OK = "SUCCESS";
    private static final String RESPONSE_STRING_ERROR = "SERVER ERROR";
    private final GeminiContentModerator moderator;

    public ReviewController(ReviewService reviewService, GeminiContentModerator moderator) {
        this.reviewService = reviewService;
        this.moderator = moderator;
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
    public ResponseEntity<ApiRespDTO<?>> createReview(@RequestBody CreateReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        ApiRespDTO resp;

        if (moderator.isViolating(dto.getReviewContent())) {
            resp = new ApiRespDTO<>(false, "INAPPROPRIATE_CONTENT", "Content is appropriate", null);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(resp);
        }
        CreateReviewDTO result = reviewService.createReview(me.getUser().getUserId(), dto.getGameId(),
                dto.getReviewContent(),
                dto.isRecommended());
        resp = new ApiRespDTO<>(true, "CREATE_SUCCESS", "Creating success", result);
        return ResponseEntity.ok(resp);
    }

    /**
     * Get Review list of a Game.
     * 
     * @param gameId
     * @return
     */
    @GetMapping("/list/{gameId}")
    @PreAuthorize("permitAll")
    public ResponseEntity<List<ReviewDTO>> getReviewList(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(reviewService.getGameReviewList(gameId));
    }

    @GetMapping("/{gameId}/{authorId}/check")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> getUserReaction(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable("gameId") long reviewGameId,
            @PathVariable("authorId") long reviewAuthorId) {
        try {
            int result = reviewService.checkUserReaction(me.getUser().getUserId(), reviewGameId, reviewAuthorId);
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
    public ResponseEntity<?> updateReview(@RequestBody UpdateReviewDTO dto,
            @PathVariable("gameId") Long gameId,
            @AuthenticationPrincipal CustomUserDetail me) {

        try {
            if (moderator.isViolating(dto.getReviewContent())) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("Your review contains inappropriate content.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        UpdateReviewDTO result = reviewService.updateReview(gameId, dto, me.getUser().getUserId());
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
    public ResponseEntity<String> likeReview(@RequestBody PatchReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        try {
            reviewService.patchLikeReview(me.getUser().getUserId(), dto.getGameId(), dto.getAuthorId());
            return ResponseEntity.ok().body(RESPONSE_STRING_OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RESPONSE_STRING_ERROR);
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
    public ResponseEntity<String> dislikeReview(@RequestBody PatchReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        try {
            reviewService.patchUnLikeReview(me.getUser().getUserId(), dto.getGameId(), dto.getAuthorId());
            return ResponseEntity.ok().body(RESPONSE_STRING_OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RESPONSE_STRING_ERROR);
        }
    }

    /**
     * 
     * @param dto include ReviewGameID & ReviewAuthorID
     * @return
     */
    @PatchMapping("/vote/clean")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> reactionReview(@RequestBody PatchReviewDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        try {
            reviewService.deleteReactionReview(me.getUser().getUserId(), dto.getGameId(), dto.getAuthorId());
            return ResponseEntity.ok().body(RESPONSE_STRING_OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RESPONSE_STRING_ERROR);
        }
    }

    /**
     * Delete Review
     * 
     * @param userId
     * @return
     */
    @DeleteMapping("/delete/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable(name = "gameId") Long gameId) {
        reviewService.deleteReview(me.getUser().getUserId(), gameId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Show all Reviews
     * Added by Loc Phan
     * 
     * @author Loc Phan
     * @return list of all reviews
     */
    @GetMapping("/list-all")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

}

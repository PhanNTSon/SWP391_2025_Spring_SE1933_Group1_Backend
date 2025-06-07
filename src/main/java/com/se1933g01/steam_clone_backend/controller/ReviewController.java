package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.Review.CreateReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.ReviewDTO;
import com.se1933g01.steam_clone_backend.repository.ReviewRepo;
import com.se1933g01.steam_clone_backend.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
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
     * @param dto
     * @return
     */
    @PostMapping("/{gameId}/post-review")
    public ResponseEntity<CreateReviewDTO> createReview(@RequestBody CreateReviewDTO dto,
            @PathVariable("gameId") Long gameId) {
        CreateReviewDTO result = reviewService.createReview(dto.getUserId(), gameId, dto.getReviewContent(),
                dto.isRecommended());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{gameId}/review-list")
    public ResponseEntity<List<ReviewDTO>> getGameDetailDTO(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(reviewService.getGameReviewList(gameId));
    }
}

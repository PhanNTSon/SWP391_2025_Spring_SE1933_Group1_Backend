package com.se1933g01.steam_clone_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steam_clone_backend.dto.Review.CreateReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.ReviewDTO;
import com.se1933g01.steam_clone_backend.dto.Review.UpdateReviewDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.entity.game.ReviewKey;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.ReviewRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

/**
 * Author: Phan Son
 */
@Service
public class ReviewService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private final GameRepo gameRepo;

    @Autowired
    private final ReviewRepo reviewRepo;

    public ReviewService(GameRepo gameRepo, ReviewRepo reviewRepo) {
        this.gameRepo = gameRepo;
        this.reviewRepo = reviewRepo;
    }

    /**
     * Create new Review
     * 
     * @param userId
     * @param gameId
     * @param reviewContent
     * @param isRecommended
     * @return
     */
    @Transactional
    public CreateReviewDTO createReview(Long userId, Long gameId, String reviewContent, boolean isRecommended) {
        User user = entityManager.getReference(User.class, userId);
        Game game = entityManager.getReference(Game.class, gameId);

        ReviewKey reviewKey = new ReviewKey(gameId, userId);

        Review review = new Review(
                reviewKey,
                reviewContent,
                LocalDate.now(),
                isRecommended,
                0,
                0,
                user,
                game);

        reviewRepo.save(review);

        return new CreateReviewDTO(isRecommended, userId, reviewContent);
    }

    /**
     * Get list of Reviews of a Game.
     * 
     * @param gameId
     * @return
     */
    public List<ReviewDTO> getGameReviewList(Long gameId) {
        Game targetGame = gameRepo.findById(gameId).orElse(null);
        if (targetGame != null) {
            List<ReviewDTO> reviews = new ArrayList<>();
            reviewRepo.findByGame_GameId(gameId).forEach(review -> {
                ReviewDTO reviewDTO = new ReviewDTO(
                        review.getUser().getUsername(),
                        review.getUser().getUserID(),
                        review.getReviewContent(),
                        review.isRecommended(),
                        review.getHelpful(),
                        review.getNotHelpful(),
                        review.getTimeCreated());
                reviews.add(reviewDTO);
            });
            return reviews;
        } else {
            return null;
        }

    }

    @Transactional
    public UpdateReviewDTO updateReview(long gameId, UpdateReviewDTO dto) {
        ReviewKey key = new ReviewKey(gameId, dto.getUserId());
        Review target = reviewRepo.findById(key).orElse(null);
        if (target != null) {
            target.setHelpful(dto.getHelpful());
            target.setNotHelpful(dto.getNotHelpful());
            target.setReviewContent(dto.getReviewContent());
            target.setRecommended(dto.isRecommended());
            reviewRepo.save(target);

            return dto;
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteReview(Long userId, Long gameId) {
        ReviewKey key = new ReviewKey(gameId, userId);
        Review review = reviewRepo.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewRepo.delete(review);
    }

}

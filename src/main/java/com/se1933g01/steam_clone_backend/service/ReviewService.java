package com.se1933g01.steam_clone_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.se1933g01.steam_clone_backend.repository.UserRepo;

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
    private final UserRepo userRepo;

    @Autowired
    private final ReviewRepo reviewRepo;

    public ReviewService(GameRepo gameRepo, UserRepo userRepo, ReviewRepo reviewRepo) {
        this.gameRepo = gameRepo;
        this.userRepo = userRepo;
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
                user,
                game,
                new HashSet<>(),
                new HashSet<>());

        reviewRepo.save(review);

        return new CreateReviewDTO(gameId, isRecommended, reviewContent);
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
                ReviewDTO reviewDTO = new ReviewDTO();

                reviewDTO.setUserId(review.getUser().getUserId());
                reviewDTO.setUserName(review.getUser().getUsername());
                reviewDTO.setRecommended(review.isRecommended());
                reviewDTO.setReviewContent(review.getReviewContent());
                reviewDTO.setHelpful(reviewRepo.countLikedByUsers(gameId, review.getUser().getUserId()));
                reviewDTO.setNotHelpful(reviewRepo.countUnLikedByUsers(gameId, review.getUser().getUserId()));
                reviewDTO.setTimeCreated(review.getTimeCreated());

                reviews.add(reviewDTO);
            });
            return reviews;
        } else {
            return null;
        }

    }

    public int checkUserReaction(long userId, long reviewGameId, long reviewAuthorId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ReviewKey key = new ReviewKey(reviewGameId, reviewAuthorId);
        Review review = reviewRepo.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        int result;

        if (user.getLikedReviews().contains(review)) {
            result = 1;
        } else if (user.getUnlikedReviews().contains(review)) {
            result = -1;
        } else {
            result = 0;
        }
        return result;
    }

    @Transactional
    public UpdateReviewDTO updateReview(long gameId, UpdateReviewDTO dto) {
        ReviewKey key = new ReviewKey(gameId, dto.getAuthorId());
        Review target = reviewRepo.findById(key).orElse(null);
        if (target != null) {

            target.setReviewContent(dto.getReviewContent());
            target.setRecommended(dto.isRecommended());

            reviewRepo.save(target);

            return dto;
        } else {
            return null;
        }
    }

    @Transactional
    public void patchLikeReview(long userId, long reviewGameId, long reviewAuthorId) {
        User user = userRepo.findById(userId).orElseThrow();
        ReviewKey key = new ReviewKey(reviewGameId, reviewAuthorId);
        Review review = reviewRepo.findById(key).orElseThrow();

        review.getLikedByUsers().add(user);
        user.getLikedReviews().add(review);

        userRepo.save(user);
    }

    @Transactional
    public void patchUnLikeReview(long userId, long reviewGameId, long reviewAuthorId) {
        User user = userRepo.findById(userId).orElseThrow();
        ReviewKey key = new ReviewKey(reviewGameId, reviewAuthorId);
        Review review = reviewRepo.findById(key).orElseThrow();

        review.getUnlikedByUsers().add(user);
        user.getUnlikedReviews().add(review);

        userRepo.save(user);
    }

    @Transactional
    public void deleteReactionReview(long userId, long reviewGameId, long reviewAuthorId) {
        User user = userRepo.findById(userId).orElseThrow();
        ReviewKey key = new ReviewKey(reviewGameId, reviewAuthorId);
        Review review = reviewRepo.findById(key).orElseThrow();

        review.getLikedByUsers().remove(user);
        user.getLikedReviews().remove(review);

        review.getUnlikedByUsers().remove(user);
        user.getUnlikedReviews().remove(review);

        userRepo.save(user);
    }

    @Transactional
    public void deleteReview(Long userId, Long gameId) {
        ReviewKey key = new ReviewKey(gameId, userId);
        Review review = reviewRepo.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewRepo.deleteAllLiked(gameId, userId);
        reviewRepo.deleteAllUnLiked(gameId, userId);
        reviewRepo.delete(review);
    }

}

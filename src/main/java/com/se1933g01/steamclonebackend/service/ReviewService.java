package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.review.CreateReviewDTO;
import com.se1933g01.steamclonebackend.dto.review.ReviewDTO;
import com.se1933g01.steamclonebackend.dto.review.ReviewSocketDTO;
import com.se1933g01.steamclonebackend.dto.review.UpdateReviewDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Review;
import com.se1933g01.steamclonebackend.entity.game.ReviewKey;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.ReviewRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

/**
 * Author: Phan Son
 */
@Service
public class ReviewService {

    private final String TYPE_UPDATE_REACTIONS = "UPDATE_REACTION";
    private final String TYPE_UPDATE_CONTENT = "UPDATE_CONTENT";
    private final String TYPE_NEW_REVIEW = "NEW_REVIEW";
    private final String TYPE_DELETE_REVIEW = "DELETE_REVIEW";

    @PersistenceContext
    private EntityManager entityManager;

    private final GameRepo gameRepo;

    private final UserRepo userRepo;

    private final ReviewRepo reviewRepo;

    private final SimpMessagingTemplate simp;

    public ReviewService(EntityManager entityManager, GameRepo gameRepo, UserRepo userRepo, ReviewRepo reviewRepo,
            SimpMessagingTemplate simp) {
        this.entityManager = entityManager;
        this.gameRepo = gameRepo;
        this.userRepo = userRepo;
        this.reviewRepo = reviewRepo;
        this.simp = simp;
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

        broadcastToChannels(review, TYPE_NEW_REVIEW);

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
                reviewDTO.setAuthorId(review.getUser().getUserId());
                reviewDTO.setGameId(review.getId().getGameId());
                reviewDTO.setAuthorName(review.getUser().getUsername());
                reviewDTO.setAuthorAvatarUrl(review.getUser().getAvatarUrl());
                reviewDTO.setRecommended(review.isRecommended());
                reviewDTO.setReviewContent(review.getReviewContent());
                reviewDTO.setHelpful(reviewRepo.countLikedByUsers(gameId, review.getUser().getUserId()));
                reviewDTO.setNotHelpful(reviewRepo.countUnLikedByUsers(gameId, review.getUser().getUserId()));
                reviewDTO.setTimeCreated(review.getTimeCreated());

                reviews.add(reviewDTO);
            });
            return reviews;
        } else {
            return new ArrayList<>();
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
    public UpdateReviewDTO updateReview(long gameId, UpdateReviewDTO dto, long authorId) {
        ReviewKey key = new ReviewKey(gameId, authorId);
        Review target = reviewRepo.findById(key).orElse(null);
        if (target != null) {

            target.setReviewContent(dto.getReviewContent());
            target.setRecommended(dto.isRecommended());
            reviewRepo.save(target);

            broadcastToChannels(target, TYPE_UPDATE_CONTENT);
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

        broadcastToChannels(review, TYPE_UPDATE_REACTIONS);
    }

    @Transactional
    public void patchUnLikeReview(long userId, long reviewGameId, long reviewAuthorId) {
        User user = userRepo.findById(userId).orElseThrow();
        ReviewKey key = new ReviewKey(reviewGameId, reviewAuthorId);
        Review review = reviewRepo.findById(key).orElseThrow();

        review.getUnlikedByUsers().add(user);
        user.getUnlikedReviews().add(review);

        userRepo.save(user);
        broadcastToChannels(review, TYPE_UPDATE_REACTIONS);

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

        broadcastToChannels(review, TYPE_UPDATE_REACTIONS);
    }

    @Transactional
    public void deleteReview(Long userId, Long gameId) {
        ReviewKey key = new ReviewKey(gameId, userId);
        Review review = reviewRepo.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewRepo.deleteAllLiked(gameId, userId);
        reviewRepo.deleteAllUnLiked(gameId, userId);
        reviewRepo.delete(review);

        broadcastToChannels(review, TYPE_DELETE_REVIEW);
    }

    private void broadcastToChannels(Review nReview, String type) {
        ReviewSocketDTO dto = new ReviewSocketDTO();
        dto.setAuthorId(nReview.getId().getUserId());
        dto.setGameId(nReview.getId().getGameId());
        dto.setNewContent(nReview.getReviewContent());
        dto.setNewRecommended(nReview.isRecommended());
        dto.setNewLikeCount(nReview.getLikedByUsers().size());
        dto.setNewUnLikeCount(nReview.getUnlikedByUsers().size());
        dto.setType(type);
        simp.convertAndSend("/topic/review." + nReview.getId().getGameId(), dto);
    }
}

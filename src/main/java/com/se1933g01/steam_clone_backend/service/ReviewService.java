package com.se1933g01.steam_clone_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steam_clone_backend.dto.Review.ReviewDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.entity.game.ReviewKey;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.ReviewRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

    @Transactional
    public Review createReview(Long userId, Long gameId, String reviewContent) {
        User user = entityManager.getReference(User.class, userId);
        Game game = entityManager.getReference(Game.class, gameId);

        ReviewKey reviewKey = new ReviewKey(gameId, userId);

        Review review = new Review();
        review.setId(reviewKey);
        review.setUser(user);
        review.setGame(game);
        review.setReviewContent(reviewContent);
        review.setTimeCreated(LocalDate.now());
        review.setRecommended(false);
        review.setHelpful(0);
        review.setNotHelpful(0);

        reviewRepo.save(review);
        return review;
    }

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

}

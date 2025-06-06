package com.se1933g01.steam_clone_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se1933g01.steam_clone_backend.dto.ReviewDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.ReviewRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

/**
 * Author: Phan Son
 */
@Service
public class GameService {
    @Autowired
    private final GameRepo gameRepo;

    @Autowired
    private final ReviewRepo reviewRepo;

    @Autowired
    private final UserRepo userRepo;

    public GameService(GameRepo gameRepo, ReviewRepo reviewRepo, UserRepo userRepo) {
        this.gameRepo = gameRepo;
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
    }

    public List<ReviewDTO> getGameReviews(Long gameId) {
        Game targetGame = gameRepo.findById(gameId).orElse(null);
        if (targetGame != null) {
            List<ReviewDTO> reviews = new ArrayList<>();
            reviewRepo.findByGame_GameId(gameId).forEach(review -> {
                ReviewDTO reviewDTO = new ReviewDTO(review.getUser().getUsername(),
                        review.getReviewContent(),
                        review.isRecommended(),
                        review.getHelpful(),
                        review.getNotHelpful());
                reviews.add(reviewDTO);
            });
            return reviews;
        } else {
            return null;
        }

    }

    

}

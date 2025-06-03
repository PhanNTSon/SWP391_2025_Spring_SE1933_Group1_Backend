package com.se1933g01.steam_clone_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.dto.ReviewDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.ReviewRepo;

@Service
public class GameService {
    @Autowired
    private final GameRepo gameRepo;

    @Autowired
    private final ReviewRepo reviewRepo;

    public GameService(GameRepo gameRepo, ReviewRepo reviewRepo) {
        this.gameRepo = gameRepo;
        this.reviewRepo = reviewRepo;
    }

    public GameDetailDTO getGame(Long gameId) {
        Game targetGame = gameRepo.findById(gameId).orElse(null);
        if (targetGame != null) {
            List<ReviewDTO> reviews = new ArrayList<>();
            reviewRepo.findByGame_GameId(gameId).forEach(review -> {
                ReviewDTO reviewDTO = new ReviewDTO(review.getUser().getUsername(), review.getReviewContent(),
                        review.isRecommended(), review.getHelpful(), review.getNotHelpful());
                        reviews.add(reviewDTO);
            });
            GameDetailDTO gameDetailDTO = new GameDetailDTO(targetGame.getName(),
                    targetGame.getReleaseDate(),
                    targetGame.getPrice(),
                    targetGame.getShortDescription(),
                    targetGame.getFullDescription(),
                    targetGame.getPublisher().getPublisherName(),
                    reviews);
            return gameDetailDTO;
        } else {
            return null;
        }

    }

}

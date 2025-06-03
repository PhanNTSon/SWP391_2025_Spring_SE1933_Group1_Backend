package com.se1933g01.steam_clone_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.entity.game.ReviewKey;

public interface ReviewRepo extends JpaRepository<Review,ReviewKey>{
    List<Review> findByGame_GameId(Long gameId);
}

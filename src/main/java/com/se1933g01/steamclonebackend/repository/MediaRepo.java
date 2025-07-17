package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;

import jakarta.transaction.Transactional;

public interface MediaRepo extends JpaRepository<Media,Long> {
    @Transactional
    void deleteByGame(Game game);
}

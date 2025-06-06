package com.se1933g01.steam_clone_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.se1933g01.steam_clone_backend.entity.game.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAll();
    
}
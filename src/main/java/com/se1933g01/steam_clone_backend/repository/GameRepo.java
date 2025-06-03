package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steam_clone_backend.entity.game.Game;

public interface GameRepo extends JpaRepository<Game,Long>{
    
}

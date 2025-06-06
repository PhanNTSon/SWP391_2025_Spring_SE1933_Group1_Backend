package com.se1933g01.steam_clone_backend.dto;

import java.util.Set;

import com.se1933g01.steam_clone_backend.entity.game.Game;

public class CartDTO {
    private Long userId;
    private Set<Game> games;
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }
}

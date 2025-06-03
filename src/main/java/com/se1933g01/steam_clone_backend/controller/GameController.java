package com.se1933g01.steam_clone_backend.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.ReviewDTO;
import com.se1933g01.steam_clone_backend.service.GameService;
/**
 * Author: Phan son
 */

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{gameId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getGameDetailDTO(@PathVariable("gameId") Long gameId){
        return ResponseEntity.ok(gameService.getGameReviews(gameId));
    }
    
    
}

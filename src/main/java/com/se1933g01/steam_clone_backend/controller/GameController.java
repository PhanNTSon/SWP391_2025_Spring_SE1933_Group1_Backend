package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.service.GameService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // @GetMapping
    // public ResponseEntity<List<GameBasicDTO>> getAllGames() {
    // List<GameBasicDTO> games = gameService.getAllGamesBasic();
    // if (games.isEmpty()) {
    // return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    // }
    // return new ResponseEntity<>(games, HttpStatus.OK);
    // }

    @GetMapping
    public ResponseEntity<List<GameBasicDTO>> getFilteredGames(
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<Integer> tags,
            @RequestParam(required = false) List<Long> publishers) {
        List<GameBasicDTO> games = gameService.findGamesByCriteria(maxPrice, tags, publishers);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailDTO> getGameById(@PathVariable Long id) {
        try {
            GameDetailDTO gameDetail = gameService.getGameDetailsById(id);
            return new ResponseEntity<>(gameDetail, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

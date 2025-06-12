package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.service.GameService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    // @GetMapping
    // public ResponseEntity<List<GameBasicDTO>> getFilteredGames(
    // @RequestParam(required = false) Double maxPrice,
    // @RequestParam(required = false) List<Integer> tags,
    // @RequestParam(required = false) List<Long> publishers) {
    // List<GameBasicDTO> games = gameService.findGamesByCriteria(maxPrice, tags,
    // publishers);
    // return new ResponseEntity<>(games, HttpStatus.OK);
    // }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailDTO> getGameById(@PathVariable Long id) {
        try {
            GameDetailDTO gameDetail = gameService.getGameDetailsById(id);
            return new ResponseEntity<>(gameDetail, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameBasicDTO>> searchGames(@RequestParam String term) {
        List<GameBasicDTO> games = gameService.searchGamesByName(term);
        return ResponseEntity.ok(games);
    }

    @GetMapping
    public ResponseEntity<Page<GameBasicDTO>> getFilteredGames(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<Integer> tags,
            @RequestParam(required = false) List<Long> publishers,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortOrder = Sort.by(direction, sort);

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<GameBasicDTO> gamePage = gameService.findGamesByCriteria(searchTerm, maxPrice, tags, publishers, pageable);

        return ResponseEntity.ok(gamePage);
    }

}

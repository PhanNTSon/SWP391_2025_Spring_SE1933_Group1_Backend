package com.se1933g01.steamclonebackend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.se1933g01.steamclonebackend.dto.AddingGameRequestDTO;
import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.service.GameService;
import com.se1933g01.steamclonebackend.service.PublisherService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/game")
public class GameController {
    Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private final GameService gameService;
    private final PublisherService publisherService;

    @Autowired
    public GameController(GameService gameService, PublisherService publisherService) {
        this.gameService = gameService;
        this.publisherService = publisherService;
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<GameDetailDTO> getGameDetailById(@PathVariable Long id) {
        try {
            GameDetailDTO gameDetail = gameService.getGameDetailsById(id);
            System.out.println("gameDetail" + gameDetail);
            return new ResponseEntity<>(gameDetail, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<GameBasicDTO>> searchGames(@RequestParam String term) {
        List<GameBasicDTO> games = gameService.searchGamesByName(term);
        return ResponseEntity.ok(games);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GameBasicDTO>> getFilteredGames(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) BigDecimal maxPrice, // Changed by Phan Son 21-06
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

    /**
     * @author Hoang VUBE
     * @param fileId
     * @return
     * @throws IOException
     */
    @GetMapping("/download/{fileId}")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = "";
        logger.info("downloadUrl" + downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }

    @GetMapping("/publisher/listed")
    public ResponseEntity<Page<GameBasicDTO>> getMyGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal CustomUserDetail userDetails) {

        Pageable pageable = PageRequest.of(page, size);
        Publisher publisher = publisherService.findById(userDetails.getUser().getUserId());

        Page<GameBasicDTO> gamePage = gameService.getGamesByPublisherApproved(publisher, name, pageable);

        return ResponseEntity.ok(gamePage);
    }

    @GetMapping("/publisher/pending")
    public ResponseEntity<Page<AddingGameRequestDTO>> getPendingAddingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal CustomUserDetail userDetails) {

        Long publisherId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<AddingGameRequestDTO> result = gameService.getPendingRequestsByPublisher(publisherId, name, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/publisher/declined")
    public ResponseEntity<Page<AddingGameRequestDTO>> getDeclinedAddingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @AuthenticationPrincipal CustomUserDetail userDetails) {

        Long publisherId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<AddingGameRequestDTO> result = gameService.getDeclinedRequestsByPublisher(publisherId, name, pageable);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/hide/{gameId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Void> hideGame(@PathVariable Long gameId) {
        gameService.hideGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/unhide/{gameId}")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Void> unhideGame(@PathVariable Long gameId) {
        gameService.unhideGame(gameId);
        return ResponseEntity.noContent().build();
    }

}

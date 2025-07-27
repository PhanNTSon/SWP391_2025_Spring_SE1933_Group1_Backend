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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.se1933g01.steamclonebackend.dto.AddingGameRequestDTO;
import com.se1933g01.steamclonebackend.dto.ApiRespDTO;
import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.dto.GamePresentDTO;
import com.se1933g01.steamclonebackend.dto.NewsDTO;
import com.se1933g01.steamclonebackend.entity.news.News;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.service.GameService;
import com.se1933g01.steamclonebackend.service.PublisherService;

@RestController
@RequestMapping("/game")
public class GameController {
    Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private final GameService gameService;
    private final PublisherService publisherService;

    public GameController(GameService gameService, PublisherService publisherService) {
        this.gameService = gameService;
        this.publisherService = publisherService;
    }

    @GetMapping("/under5")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiRespDTO<?>> getGameUnder5() {
        List<GamePresentDTO> resp = gameService.getGamesUnder5();
        return ResponseEntity.ok().body(new ApiRespDTO<List<GamePresentDTO>>(true, "GET_SUCCESS", "Get success", resp));
    }

    @GetMapping("/new-publish")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiRespDTO<?>> getNewPublishGame() {
        List<GamePresentDTO> resp = gameService.getNewPublishGames();
        return ResponseEntity.ok().body(new ApiRespDTO<List<GamePresentDTO>>(true, "GET_SUCCESS", "Get success", resp));

    }

    @GetMapping("/top-selling")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiRespDTO<?>> getTopSellingGame() {
        List<GamePresentDTO> resp = gameService.getTopSellingGames();
        return ResponseEntity.ok().body(new ApiRespDTO<List<GamePresentDTO>>(true, "GET_SUCCESS", "Get success", resp));

    }

    @GetMapping("/most-rated")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiRespDTO<?>> getMostRatedGames() {
        List<GamePresentDTO> resp = gameService.getMostRatedGames();
        return ResponseEntity.ok().body(new ApiRespDTO<List<GamePresentDTO>>(true, "GET_SUCCESS", "Get success", resp));

    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<GameDetailDTO> getGameDetailById(@PathVariable Long id) {
        try {
            GameDetailDTO gameDetail = gameService.getGameDetailsById(id);
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

    @GetMapping("/tag/{tagId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GameBasicDTO>> getGamesByTagId(@PathVariable Integer tagId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<Integer> tagIds = List.of(tagId);
        String searchterm = null;
        BigDecimal maxPrice = null;
        List<Long> publishers = null;
        Boolean state = true;
        Pageable pageable = PageRequest.of(page, size);
        Page<GameBasicDTO> games = gameService.findGamesByCriteria(searchterm, maxPrice, tagIds, publishers, pageable, state);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/publisher/{publisherId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GameBasicDTO>> getGamesByPublisherId(@PathVariable Integer publisherId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
                Long publisherIdLong = Long.valueOf(publisherId);
        List<Long> publisherIds = List.of(publisherIdLong);
        String searchterm = null;
        BigDecimal maxPrice = null;
        List<Integer> tags = null;
        Boolean state = true;
        Pageable pageable = PageRequest.of(page, size);
        Page<GameBasicDTO> games = gameService.findGamesByCriteria(searchterm, maxPrice, tags, publisherIds, pageable, state);
        return ResponseEntity.ok(games);
    }
    

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GameBasicDTO>> getFilteredGames(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<Integer> tags,
            @RequestParam(required = false) List<Long> publishers,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortOrder = Sort.by(direction, sort);
        Boolean state = true;

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<GameBasicDTO> gamePage = gameService.findGamesByCriteria(searchTerm, maxPrice, tags, publishers, pageable, state);
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
    @PreAuthorize("hasAnyRole('PUBLISHER','ADMIN')")
    public ResponseEntity<Void> hideGame(@PathVariable Long gameId) {
        gameService.hideGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/unhide/{gameId}")
    @PreAuthorize("hasAnyRole('PUBLISHER','ADMIN')")
    public ResponseEntity<Void> unhideGame(@PathVariable Long gameId) {
        gameService.unhideGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/related/tags/{gameId}")
    public ResponseEntity<List<GameBasicDTO>> getRelatedGames(@PathVariable Long gameId) {
        List<GameBasicDTO> recommendedGames = gameService.getTagBasedRecommendations(gameId);
        return ResponseEntity.ok(recommendedGames);
    }

    @GetMapping("/related/owned/{userId}")
    public ResponseEntity<List<GameBasicDTO>> getLibraryBasedRecommendations(@PathVariable Long userId) {
        List<GameBasicDTO> recommendedGames = gameService.recommendSimilarToLibrary(userId);
        return ResponseEntity.ok(recommendedGames);
    }

    @GetMapping("/news/{gameId}")
    public ResponseEntity<Page<NewsDTO>> getPagedNewsByGameId(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<NewsDTO> newsPage = gameService.getPagedNewsByGameId(gameId, page, size);
        return ResponseEntity.ok(newsPage);
    }

    @PostMapping("/news/{gameId}/create")
    public ResponseEntity<NewsDTO> createNews(
            @PathVariable Long gameId,
            @RequestBody NewsDTO newsDTO) {

        NewsDTO created = gameService.createNews(gameId, newsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/news/view/{id}")
    public ResponseEntity<NewsDTO> getNewsDetails(@PathVariable Long id) {
        NewsDTO news = gameService.getNewsDetails(id);
        return ResponseEntity.ok(news);
    }

    @PutMapping("/news/edit/{newsId}")
    public ResponseEntity<?> editNews(
            @PathVariable Long newsId,
            @RequestBody NewsDTO newsDto) {

        try {
            News updatedNews = gameService.updateNews(newsId, newsDto);
            return ResponseEntity.ok(updatedNews);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("News not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update news");
        }
    }

    @DeleteMapping("/news/delete/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        gameService.deleteNewsById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listed")
    public ResponseEntity<Page<GameBasicDTO>> getListedGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameBasicDTO> gamePage = gameService.getListedGame(name, pageable);
        return ResponseEntity.ok(gamePage);
    }

    @GetMapping("/hidden")
    public ResponseEntity<Page<GameBasicDTO>> getHiddenGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GameBasicDTO> gamePage = gameService.getHiddenGame(name, pageable);
        return ResponseEntity.ok(gamePage);
    }
}

package com.se1933g01.steamclonebackend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.mapper.EntityMapper;
import com.se1933g01.steamclonebackend.repository.GameRepository;
import com.se1933g01.steamclonebackend.specification.GameSpecification;
import com.se1933g01.steamclonebackend.service.GameService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class) // Tích hợp Mockito và JUnit 5
class GameServiceTest {

    @Mock 
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game game1;
    private Game game2;

    @BeforeEach
    void setUp() {
        game1 = new Game();
        game1.setGameId(1L);
        game1.setName("Test Game 1");
        game1.setPrice(new BigDecimal("49.99"));

        game2 = new Game();
        game2.setGameId(2L);
        game2.setName("Another Game");
        game2.setPrice(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("getGameDetailsById - Should return GameDetailDTO when game exists")
    void getGameDetailsById_whenGameExists_shouldReturnGameDetailDTO() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game1));
        GameDetailDTO resultDTO = gameService.getGameDetailsById(1L);
        assertNotNull(resultDTO);
        assertEquals(game1.getGameId(), resultDTO.getGameId());
        assertEquals(game1.getName(), resultDTO.getName());
    }

    @Test
    @DisplayName("getGameDetailsById - Should throw EntityNotFoundException when game does not exist")
    void getGameDetailsById_whenGameDoesNotExist_shouldThrowException() {
        when(gameRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            gameService.getGameDetailsById(99L);
        });
    }

    @Test
    @DisplayName("searchGamesByName - Should return a list of games when term matches")
    void searchGamesByName_whenTermMatches_shouldReturnGameList() {
        List<Game> games = List.of(game1);
        Page<Game> gamePage = new PageImpl<>(games);
        Pageable pageable = PageRequest.of(0, 5);
        when(gameRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(gamePage);
        List<GameBasicDTO> result = gameService.searchGamesByName("Test");
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Game 1", result.get(0).getTitle());
    }

    @Test
    @DisplayName("searchGamesByName - Should return empty list for null or empty term")
    void searchGamesByName_whenTermIsEmpty_shouldReturnEmptyList() {
        List<GameBasicDTO> resultForNull = gameService.searchGamesByName(null);
        List<GameBasicDTO> resultForEmpty = gameService.searchGamesByName("  ");
        assertTrue(resultForNull.isEmpty());
        assertTrue(resultForEmpty.isEmpty());
    }

    @Test
    @DisplayName("findGamesByCriteria - Should return games matching all criteria")
    @SuppressWarnings("unchecked")
    void findGamesByCriteria_whenAllCriteriaProvided_shouldReturnMatchingGames() {
        List<Game> games = List.of(game1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Game> gamePage = new PageImpl<>(games, pageable, 1);
        when(gameRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(gamePage);
        Page<GameBasicDTO> result = gameService.findGamesByCriteria("Test", new BigDecimal("50.00"),
                List.of(1, 2), List.of(3L), pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Game 1", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("findGamesByCriteria - Should return empty page when no games match")
    @SuppressWarnings("unchecked")
    void findGamesByCriteria_whenNoGamesMatch_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Game> emptyPage = Page.empty(pageable);
        when(gameRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        Page<GameBasicDTO> result = gameService.findGamesByCriteria("NonExistent", null, null, null, pageable);

        assertTrue(result.isEmpty());
    }
}

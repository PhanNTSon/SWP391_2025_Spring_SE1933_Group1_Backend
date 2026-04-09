package com.se1933g01.steamclonebackend.service;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.repository.GameRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void getAllGamesBasic_shouldReturnMappedGameBasicDTOs() {
        // ===== Arrange =====
        Game game1 = new Game();
        game1.setGameId(1L);
        game1.setName("Half-Life");
        game1.setPrice(new BigDecimal("199000"));

        Game game2 = new Game();
        game2.setGameId(2L);
        game2.setName("Portal");
        game2.setPrice(new BigDecimal("99000"));

        when(gameRepository.findAll())
                .thenReturn(List.of(game1, game2));

        // ===== Act =====
        List<GameBasicDTO> result = gameService.getAllGamesBasic();

        // ===== Assert =====
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // Game 1
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("Half-Life");
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("199000");

        // Game 2
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("Portal");
        assertThat(result.get(1).getPrice()).isEqualByComparingTo("99000");

        // verify repository call
        verify(gameRepository).findAll();
    }

    @Test
    void getAllGamesBasic_whenNoGame_shouldReturnEmptyList() {
        // ===== Arrange =====
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // ===== Act =====
        List<GameBasicDTO> result = gameService.getAllGamesBasic();

        // ===== Assert =====
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(gameRepository).findAll();
    }
}
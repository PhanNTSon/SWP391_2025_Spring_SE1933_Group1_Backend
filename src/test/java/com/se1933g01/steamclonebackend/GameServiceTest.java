package com.se1933g01.steamclonebackend.service;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.repository.GameRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void getAllGamesBasic_shouldReturnMappedDTOs() {
        // Arrange: tạo dữ liệu giả (entity) trả về từ repository
        Game game1 = new Game();
        game1.setGameId(1L);
        game1.setName("Game One");
        game1.setPrice(10.0);

        Game game2 = new Game();
        game2.setGameId(2L);
        game2.setName("Game Two");
        game2.setPrice(20.0);

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));

        // Act: gọi method thật của service (đây mới là "test")
        List<GameBasicDTO> result = gameService.getAllGamesBasic();

        // Assert: kiểm tra kết quả mapping
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // Kiểm tra DTO 1
        assertThat(result.get(0).getGameId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Game One");
        // nếu GameBasicDTO có price thì mở dòng này
        assertThat(result.get(0).getPrice()).isEqualTo(10.0);

        // Kiểm tra DTO 2
        assertThat(result.get(1).getGameId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("Game Two");
        assertThat(result.get(1).getPrice()).isEqualTo(20.0);

        // Verify: đảm bảo repository được gọi đúng
        verify(gameRepository).findAll();
    }

    @Test
    void getAllGamesBasic_whenRepositoryReturnsEmpty_shouldReturnEmptyList() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<GameBasicDTO> result = gameService.getAllGamesBasic();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // Verify
        verify(gameRepository).findAll();
    }
}
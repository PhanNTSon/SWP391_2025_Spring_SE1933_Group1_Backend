package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.se1933g01.steamclonebackend.dto.CartDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.LibraryRepository;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.service.CartService;
import com.se1933g01.steamclonebackend.service.LibraryService;

public class CartServiceTest {

    private UserRepo userRepo;
    private GameRepo gameRepo;
    private TransactionRepo transactionRepo;
    private LibraryRepository libraryRepo;
    private SimpMessagingTemplate simp;
    private LibraryService libraryService;

    private CartService cartService;

    @Before
    public void setUp() {
        userRepo = mock(UserRepo.class);
        gameRepo = mock(GameRepo.class);
        transactionRepo = mock(TransactionRepo.class);
        libraryRepo = mock(LibraryRepository.class);
        simp = mock(SimpMessagingTemplate.class);
        libraryService = mock(LibraryService.class);

        cartService = new CartService(userRepo, gameRepo, transactionRepo, simp, libraryRepo, libraryService);
    }

    @Test
    public void getCart_validUserId_returnsCorrectCartDTO() {
        // Arrange
        Long userId = 1L;

        Game game = new Game();
        game.setGameId(100L);
        game.setName("Test Game");
        game.setPrice(new BigDecimal("19.99"));

        Media media = new Media();
        media.setUrl("http://example.com/image.jpg");
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media);
        game.setMedia(mediaList);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(game);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(cartGames);

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        CartDTO result = cartService.getCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getListCart().size());

        assertEquals(100L, result.getListCart().get(0).getId());
        assertEquals("Test Game", result.getListCart().get(0).getTitle());
        assertEquals("http://example.com/image.jpg", result.getListCart().get(0).getImageUrl());
        assertEquals(new BigDecimal("19.99"), result.getListCart().get(0).getPrice());
        assertEquals(BigDecimal.ZERO, result.getListCart().get(0).getDiscountPrice());
        assertEquals(new BigDecimal("19.99"), result.getListCart().get(0).getOriginalPrice());
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void getCart_userNotFound_throwsException() {
        // Arrange
        Long userId = 2L;
        when(userRepo.findByIdWithCartGames(userId)).thenReturn(null);

        // Act
        cartService.getCart(userId);

        // Assert is handled by expected exception
    }

    @Test
    public void getCart_validUserWithEmptyCart_returnsEmptyList() {
        // Arrange
        Long userId = 3L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(new HashSet<>()); // cart rỗng

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        CartDTO result = cartService.getCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getListCart().size()); // empty cart
    }
}

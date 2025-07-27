package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.se1933g01.steamclonebackend.dto.CartDTO;
import com.se1933g01.steamclonebackend.dto.user.LibraryGameDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;
import com.se1933g01.steamclonebackend.entity.transaction.Transaction;
import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.LibraryRepository;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

public class CartServiceTest {

    private UserRepo userRepo;
    private GameRepo gameRepo;
    private TransactionRepo transactionRepo;
    private LibraryRepository libraryRepo;
    private SimpMessagingTemplate simp;
    private LibraryService libraryService;
    private EmailService emailService;

    private CartService cartService;

    @Before
    public void setUp() {
        userRepo = mock(UserRepo.class);
        gameRepo = mock(GameRepo.class);
        transactionRepo = mock(TransactionRepo.class);
        libraryRepo = mock(LibraryRepository.class);
        simp = mock(SimpMessagingTemplate.class);
        libraryService = mock(LibraryService.class);
        emailService = mock(EmailService.class);
        
        cartService = new CartService(userRepo, gameRepo, transactionRepo, simp, libraryRepo, libraryService, emailService);
    }

    // ==================== getTotalGamesInCart Tests ====================

    @Test
    public void getTotalGamesInCart_validUserId_returnsCorrectCount() {
        // Arrange
        Long userId = 1L;

        Game game1 = new Game();
        game1.setGameId(100L);
        Game game2 = new Game();
        game2.setGameId(101L);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(game1);
        cartGames.add(game2);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(cartGames);

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        long result = cartService.getTotalGamesInCart(userId);

        // Assert
        assertEquals(2L, result);
    }

    @Test
    public void getTotalGamesInCart_emptyCart_returnsZero() {
        // Arrange
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        long result = cartService.getTotalGamesInCart(userId);

        // Assert
        assertEquals(0L, result);
    }

    // ==================== getCart Tests ====================

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
        mockUser.setCartGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        CartDTO result = cartService.getCart(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getListCart().size());
    }

    @Test
    public void getCart_gameWithoutMedia_returnsNullImageUrl() {
        // Arrange
        Long userId = 1L;

        Game game = new Game();
        game.setGameId(100L);
        game.setName("Test Game");
        game.setPrice(new BigDecimal("19.99"));
        game.setMedia(new ArrayList<>()); // empty media list

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
        assertNull(result.getListCart().get(0).getImageUrl());
    }

    // ==================== addGameToCart Tests ====================

    @Test
    public void addGameToCart_validUserAndGame_successfullyAddsGame() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setCartGames(new HashSet<>());
        mockUser.setLibraryGames(new HashSet<>());

        Game mockGame = new Game();
        mockGame.setGameId(gameId);
        mockGame.setName("Test Game");
        mockGame.setPrice(new BigDecimal("19.99"));

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(mockGame));
        when(userRepo.save(mockUser)).thenReturn(mockUser);

        // Act
        CartDTO result = cartService.addGameToCart(userId, gameId);

        // Assert
        assertNotNull(result);
        verify(userRepo).save(mockUser);
        verify(simp).convertAndSendToUser(eq("testuser"), eq("/queue/cart.count"), eq(1));
        assertTrue(mockUser.getCartGames().contains(mockGame));
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void addGameToCart_userNotFound_throwsException() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(null);

        // Act
        cartService.addGameToCart(userId, gameId);

        // Assert is handled by expected exception
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void addGameToCart_gameNotFound_throwsException() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(new HashSet<>());
        mockUser.setLibraryGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(gameRepo.findById(gameId)).thenReturn(Optional.empty());

        // Act
        cartService.addGameToCart(userId, gameId);

        // Assert is handled by expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGameToCart_gameAlreadyInCart_throwsException() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        Game existingGame = new Game();
        existingGame.setGameId(gameId);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(existingGame);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(cartGames);
        mockUser.setLibraryGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        cartService.addGameToCart(userId, gameId);

        // Assert is handled by expected exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void addGameToCart_userAlreadyOwnsGame_throwsException() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        Game ownedGame = new Game();
        ownedGame.setGameId(gameId);

        Library libraryItem = new Library();
        libraryItem.setGame(ownedGame);

        Set<Library> libraryGames = new HashSet<>();
        libraryGames.add(libraryItem);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(new HashSet<>());
        mockUser.setLibraryGames(libraryGames);

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        cartService.addGameToCart(userId, gameId);

        // Assert is handled by expected exception
    }

    // ==================== removeGameFromCart Tests ====================

    @Test
    public void removeGameFromCart_validUserAndGame_successfullyRemovesGame() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        Game gameToRemove = new Game();
        gameToRemove.setGameId(gameId);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(gameToRemove);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setCartGames(cartGames);

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(userRepo.save(mockUser)).thenReturn(mockUser);

        // Act
        CartDTO result = cartService.removeGameFromCart(userId, gameId);

        // Assert
        assertNotNull(result);
        verify(userRepo).save(mockUser);
        verify(simp).convertAndSendToUser(eq("testuser"), eq("/queue/cart.count"), eq(0));
        assertFalse(mockUser.getCartGames().stream().anyMatch(g -> g.getGameId().equals(gameId)));
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void removeGameFromCart_userNotFound_throwsException() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(null);

        // Act
        cartService.removeGameFromCart(userId, gameId);

        // Assert is handled by expected exception
    }

    @Test
    public void removeGameFromCart_gameNotInCart_noException() {
        // Arrange
        Long userId = 1L;
        Long gameId = 100L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setCartGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(userRepo.save(mockUser)).thenReturn(mockUser);

        // Act
        CartDTO result = cartService.removeGameFromCart(userId, gameId);

        // Assert
        assertNotNull(result);
        verify(userRepo).save(mockUser);
    }

    // ==================== calculateCartTotal Tests ====================

    @Test
    public void calculateCartTotal_validUserWithGames_returnsCorrectTotal() {
        // Arrange
        Long userId = 1L;

        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setPrice(new BigDecimal("19.99"));

        Game game2 = new Game();
        game2.setGameId(101L);
        game2.setPrice(new BigDecimal("29.99"));

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(game1);
        cartGames.add(game2);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(cartGames);

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        BigDecimal result = cartService.calculateCartTotal(userId);

        // Assert
        assertEquals(new BigDecimal("49.98"), result);
    }

    @Test
    public void calculateCartTotal_emptyCart_returnsZero() {
        // Arrange
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        BigDecimal result = cartService.calculateCartTotal(userId);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void calculateCartTotal_userNotFound_throwsException() {
        // Arrange
        Long userId = 1L;

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(null);

        // Act
        cartService.calculateCartTotal(userId);

        // Assert is handled by expected exception
    }

    // ==================== checkout Tests ====================

    @Test
    public void checkout_validUserWithSufficientBalance_successfulCheckout() {
        // Arrange
        Long userId = 1L;
        Long publisherId = 2L;

        // Create publisher user
        User publisherUser = new User();
        publisherUser.setUserId(publisherId);
        publisherUser.setWalletBalance(new BigDecimal("0.00"));

        // Create publisher
        Publisher publisher = new Publisher();
        publisher.setUser(publisherUser);

        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setPrice(new BigDecimal("19.99"));
        game1.setTotalPurchased(0);
        game1.setPublisher(publisher);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(game1);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setCartGames(cartGames);
        mockUser.setLibraryGames(new HashSet<>());
        mockUser.setWalletBalance(new BigDecimal("50.00"));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setTransactionId(1L);

        LibraryGameDTO mockLibraryGameDTO = new LibraryGameDTO();

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(userRepo.save(any(User.class))).thenReturn(mockUser);
        when(libraryService.mapLibraryEntryToDto(any(Library.class))).thenReturn(mockLibraryGameDTO);

        // Act
        CartDTO result = cartService.checkout(userId);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("30.01"), mockUser.getWalletBalance());
        assertEquals(Integer.valueOf(1), game1.getTotalPurchased());
        verify(transactionRepo, atLeast(1)).save(any(Transaction.class));
        verify(libraryRepo).save(any(Library.class));
        verify(emailService).sendPurchaseInvoiceEmail(eq("test@example.com"), eq("1"), eq("testuser"), eq("Test Game"), eq(new BigDecimal("19.99")));
        verify(simp).convertAndSendToUser(eq("testuser"), eq("/queue/cart.count"), eq(0));
        verify(simp).convertAndSendToUser(eq("testuser"), eq("/queue/wallet.balance"), eq(new BigDecimal("30.01")));
        verify(simp).convertAndSendToUser(eq("testuser"), eq("/queue/libraryItem.added"), eq(mockLibraryGameDTO));
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void checkout_userNotFound_throwsException() {
        // Arrange
        Long userId = 1L;

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(null);

        // Act
        cartService.checkout(userId);

        // Assert is handled by expected exception
    }

    @Test(expected = jakarta.persistence.EntityNotFoundException.class)
    public void checkout_emptyCart_throwsException() {
        // Arrange
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(new HashSet<>());

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        cartService.checkout(userId);

        // Assert is handled by expected exception
    }

    @Test(expected = RuntimeException.class)
    public void checkout_insufficientBalance_throwsException() {
        // Arrange
        Long userId = 1L;

        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setPrice(new BigDecimal("50.00"));

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(game1);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(cartGames);
        mockUser.setLibraryGames(new HashSet<>());
        mockUser.setWalletBalance(new BigDecimal("10.00"));

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        cartService.checkout(userId);

        // Assert is handled by expected exception
    }

    @Test(expected = RuntimeException.class)
    public void checkout_allGamesAlreadyOwned_throwsException() {
        // Arrange
        Long userId = 1L;

        Game ownedGame = new Game();
        ownedGame.setGameId(100L);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(ownedGame);

        Library libraryItem = new Library();
        libraryItem.setGame(ownedGame);

        Set<Library> libraryGames = new HashSet<>();
        libraryGames.add(libraryItem);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setCartGames(cartGames);
        mockUser.setLibraryGames(libraryGames);
        mockUser.setWalletBalance(new BigDecimal("100.00"));

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);

        // Act
        cartService.checkout(userId);

        // Assert is handled by expected exception
    }

    @Test
    public void checkout_partiallyOwnedGames_onlyBuysNewGames() {
        // Arrange
        Long userId = 1L;
        Long publisherId = 2L;

        // Create publisher user
        User publisherUser = new User();
        publisherUser.setUserId(publisherId);
        publisherUser.setWalletBalance(new BigDecimal("0.00"));

        // Create publisher
        Publisher publisher = new Publisher();
        publisher.setUser(publisherUser);

        Game ownedGame = new Game();
        ownedGame.setGameId(100L);

        Game newGame = new Game();
        newGame.setGameId(101L);
        newGame.setPrice(new BigDecimal("19.99"));
        newGame.setTotalPurchased(0);
        newGame.setPublisher(publisher);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(ownedGame);
        cartGames.add(newGame);

        Library libraryItemOwned = new Library();
        libraryItemOwned.setGame(ownedGame);

        Set<Library> libraryGames = new HashSet<>();
        libraryGames.add(libraryItemOwned);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setCartGames(cartGames);
        mockUser.setLibraryGames(libraryGames);
        mockUser.setWalletBalance(new BigDecimal("50.00"));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setTransactionId(1L);

        LibraryGameDTO mockLibraryGameDTO = new LibraryGameDTO();

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(userRepo.save(any(User.class))).thenReturn(mockUser);
        when(libraryService.mapLibraryEntryToDto(any(Library.class))).thenReturn(mockLibraryGameDTO);

        // Act
        CartDTO result = cartService.checkout(userId);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("30.01"), mockUser.getWalletBalance());
        assertEquals(Integer.valueOf(1), newGame.getTotalPurchased());
        verify(transactionRepo, atLeast(1)).save(any(Transaction.class));
        verify(libraryRepo, times(1)).save(any(Library.class));
    }

    @Test
    public void checkout_multipleGames_sendsMultiGameEmail() {
        // Arrange
        Long userId = 1L;
        Long publisherId = 2L;

        // Create publisher user
        User publisherUser = new User();
        publisherUser.setUserId(publisherId);
        publisherUser.setWalletBalance(new BigDecimal("0.00"));

        // Create publisher
        Publisher publisher = new Publisher();
        publisher.setUser(publisherUser);

        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setPrice(new BigDecimal("19.99"));
        game1.setTotalPurchased(0);
        game1.setPublisher(publisher);

        Game game2 = new Game();
        game2.setGameId(101L);
        game2.setPrice(new BigDecimal("29.99"));
        game2.setTotalPurchased(0);
        game2.setPublisher(publisher);

        HashSet<Game> cartGames = new HashSet<>();
        cartGames.add(game1);
        cartGames.add(game2);

        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setCartGames(cartGames);
        mockUser.setLibraryGames(new HashSet<>());
        mockUser.setWalletBalance(new BigDecimal("100.00"));

        Transaction mockTransaction = new Transaction();
        mockTransaction.setTransactionId(1L);

        LibraryGameDTO mockLibraryGameDTO = new LibraryGameDTO();

        when(userRepo.findByIdWithCartGames(userId)).thenReturn(mockUser);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(mockTransaction);
        when(userRepo.save(any(User.class))).thenReturn(mockUser);
        when(libraryService.mapLibraryEntryToDto(any(Library.class))).thenReturn(mockLibraryGameDTO);

        // Act
        CartDTO result = cartService.checkout(userId);

        // Assert
        verify(emailService).sendMultiGameInvoiceEmail(eq("test@example.com"), eq("1"), eq("testuser"), anyList());
    }
}
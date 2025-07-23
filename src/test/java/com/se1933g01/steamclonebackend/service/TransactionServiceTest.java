package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.se1933g01.steamclonebackend.entity.CompositedKey;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.transaction.Transaction;
import com.se1933g01.steamclonebackend.entity.transaction.TransactionDetail;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

public class TransactionServiceTest {

    private UserRepo userRepo;
    private TransactionRepo transactionRepo;
    private TransactionService transactionService;

    @Before
    public void setUp() {
        userRepo = mock(UserRepo.class);
        transactionRepo = mock(TransactionRepo.class);
        transactionService = new TransactionService(userRepo, transactionRepo);
    }

    // ==================== getTransactions Tests ====================

    @Test
    public void getTransactions_validUserId_returnsTransactionList() {
        // Arrange
        Long userId = 1L;
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setName("Test Game 1");
        
        Game game2 = new Game();
        game2.setGameId(101L);
        game2.setName("Test Game 2");
        
        // Create transaction details
        TransactionDetail detail1 = new TransactionDetail();
        CompositedKey key1 = new CompositedKey();
        key1.setKey1(1L);
        key1.setKey2(100L);
        detail1.setId(key1);
        detail1.setGame(game1);
        detail1.setPrice(new BigDecimal("19.99"));
        
        TransactionDetail detail2 = new TransactionDetail();
        CompositedKey key2 = new CompositedKey();
        key2.setKey1(2L);
        key2.setKey2(101L);
        detail2.setId(key2);
        detail2.setGame(game2);
        detail2.setPrice(new BigDecimal("29.99"));
        
        // Create transactions
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(1L);
        transaction1.setUser(mockUser);
        transaction1.setTotalAmount(new BigDecimal("19.99"));
        transaction1.setCreatedAt(LocalDate.now());
        List<TransactionDetail> details1 = new ArrayList<>();
        details1.add(detail1);
        transaction1.setTransactionDetail(details1);
        detail1.setTransaction(transaction1);
        
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(2L);
        transaction2.setUser(mockUser);
        transaction2.setTotalAmount(new BigDecimal("29.99"));
        transaction2.setCreatedAt(LocalDate.now());
        List<TransactionDetail> details2 = new ArrayList<>();
        details2.add(detail2);
        transaction2.setTransactionDetail(details2);
        detail2.setTransaction(transaction2);
        
        List<Transaction> expectedTransactions = new ArrayList<>();
        expectedTransactions.add(transaction1);
        expectedTransactions.add(transaction2);
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(expectedTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactions(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(transaction1, result.get(0));
        assertEquals(transaction2, result.get(1));
        verify(userRepo).findById(userId);
        verify(transactionRepo).findByUser(mockUser);
    }

    @Test
    public void getTransactions_validUserIdWithNoTransactions_returnsEmptyList() {
        // Arrange
        Long userId = 1L;
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        List<Transaction> emptyTransactions = new ArrayList<>();
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(emptyTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactions(userId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(userRepo).findById(userId);
        verify(transactionRepo).findByUser(mockUser);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getTransactions_userNotFound_throwsException() {
        // Arrange
        Long userId = 999L;
        
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        transactionService.getTransactions(userId);

        // Assert is handled by expected exception
    }

    @Test
    public void getTransactions_userIdIsNull_throwsException() {
        // Arrange
        Long userId = null;
        
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            transactionService.getTransactions(userId);
            fail("Expected EntityNotFoundException");
        } catch (EntityNotFoundException e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    // ==================== getTransactionDetailByTransactionId Tests ====================

    @Test
    public void getTransactionDetailByTransactionId_validUserAndTransactionId_returnsTransactionDetail() {
        // Arrange
        Long userId = 1L;
        Long transactionId = 1L;
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        Game game = new Game();
        game.setGameId(100L);
        game.setName("Test Game");
        
        TransactionDetail expectedDetail = new TransactionDetail();
        CompositedKey key = new CompositedKey();
        key.setKey1(transactionId);
        key.setKey2(100L);
        expectedDetail.setId(key);
        expectedDetail.setGame(game);
        expectedDetail.setPrice(new BigDecimal("19.99"));
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setUser(mockUser);
        transaction.setTotalAmount(new BigDecimal("19.99"));
        transaction.setCreatedAt(LocalDate.now());
        List<TransactionDetail> details = new ArrayList<>();
        details.add(expectedDetail);
        transaction.setTransactionDetail(details);
        expectedDetail.setTransaction(transaction);
        
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(transactions);

        // Act
        TransactionDetail result = transactionService.getTransactionDetailByTransactionId(userId, transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDetail, result);
        assertEquals(new BigDecimal("19.99"), result.getPrice());
        assertEquals(game, result.getGame());
    }

    @Test
    public void getTransactionDetailByTransactionId_transactionNotFound_returnsNull() {
        // Arrange
        Long userId = 1L;
        Long transactionId = 999L; // Non-existent transaction ID
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        Game game = new Game();
        game.setGameId(100L);
        game.setName("Test Game");
        
        TransactionDetail detail = new TransactionDetail();
        CompositedKey key = new CompositedKey();
        key.setKey1(1L); // Different transaction ID
        key.setKey2(100L);
        detail.setId(key);
        detail.setGame(game);
        detail.setPrice(new BigDecimal("19.99"));
        
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1L); // Different transaction ID
        transaction.setUser(mockUser);
        transaction.setTotalAmount(new BigDecimal("19.99"));
        transaction.setCreatedAt(LocalDate.now());
        List<TransactionDetail> details = new ArrayList<>();
        details.add(detail);
        transaction.setTransactionDetail(details);
        detail.setTransaction(transaction);
        
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(transactions);

        // Act
        TransactionDetail result = transactionService.getTransactionDetailByTransactionId(userId, transactionId);

        // Assert
        assertNull(result);
    }

    @Test
    public void getTransactionDetailByTransactionId_userHasNoTransactions_returnsNull() {
        // Arrange
        Long userId = 1L;
        Long transactionId = 1L;
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        List<Transaction> emptyTransactions = new ArrayList<>();
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(emptyTransactions);

        // Act
        TransactionDetail result = transactionService.getTransactionDetailByTransactionId(userId, transactionId);

        // Assert
        assertNull(result);
    }

    @Test(expected = EntityNotFoundException.class)
    public void getTransactionDetailByTransactionId_userNotFound_throwsException() {
        // Arrange
        Long userId = 999L;
        Long transactionId = 1L;
        
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        transactionService.getTransactionDetailByTransactionId(userId, transactionId);

        // Assert is handled by expected exception
    }

    @Test
    public void getTransactionDetailByTransactionId_multipleTransactions_returnsCorrectDetail() {
        // Arrange
        Long userId = 1L;
        Long targetTransactionId = 2L;
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setName("Test Game 1");
        
        Game game2 = new Game();
        game2.setGameId(101L);
        game2.setName("Test Game 2");
        
        // First transaction
        TransactionDetail detail1 = new TransactionDetail();
        CompositedKey key1 = new CompositedKey();
        key1.setKey1(1L);
        key1.setKey2(100L);
        detail1.setId(key1);
        detail1.setGame(game1);
        detail1.setPrice(new BigDecimal("19.99"));
        
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(1L);
        transaction1.setUser(mockUser);
        transaction1.setTotalAmount(new BigDecimal("19.99"));
        transaction1.setCreatedAt(LocalDate.now());
        List<TransactionDetail> details1 = new ArrayList<>();
        details1.add(detail1);
        transaction1.setTransactionDetail(details1);
        detail1.setTransaction(transaction1);
        
        // Second transaction (target)
        TransactionDetail detail2 = new TransactionDetail();
        CompositedKey key2 = new CompositedKey();
        key2.setKey1(targetTransactionId);
        key2.setKey2(101L);
        detail2.setId(key2);
        detail2.setGame(game2);
        detail2.setPrice(new BigDecimal("29.99"));
        
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(targetTransactionId);
        transaction2.setUser(mockUser);
        transaction2.setTotalAmount(new BigDecimal("29.99"));
        transaction2.setCreatedAt(LocalDate.now());
        List<TransactionDetail> details2 = new ArrayList<>();
        details2.add(detail2);
        transaction2.setTransactionDetail(details2);
        detail2.setTransaction(transaction2);
        
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(transactions);

        // Act
        TransactionDetail result = transactionService.getTransactionDetailByTransactionId(userId, targetTransactionId);

        // Assert
        assertNotNull(result);
        assertEquals(detail2, result);
        assertEquals(new BigDecimal("29.99"), result.getPrice());
        assertEquals(game2, result.getGame());
        assertTrue(targetTransactionId.equals(result.getId().getKey1()));
    }

    @Test
    public void getTransactionDetailByTransactionId_nullTransactionId_returnsNull() {
        // Arrange
        Long userId = 1L;
        Long transactionId = null;
        
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("testuser");
        
        List<Transaction> transactions = new ArrayList<>();
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(transactionRepo.findByUser(mockUser)).thenReturn(transactions);

        // Act
        TransactionDetail result = transactionService.getTransactionDetailByTransactionId(userId, transactionId);

        // Assert
        assertNull(result);
    }
}

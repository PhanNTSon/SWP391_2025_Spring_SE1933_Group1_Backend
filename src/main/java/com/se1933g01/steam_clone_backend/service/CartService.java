package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.entity.CompositedKey;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.transaction.TransactionDetail;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.TransactionRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.mockito.ArgumentMatchers.floatThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private TransactionRepo transactionRepo;

    public CartService(UserRepo userRepo, GameRepo gameRepo, TransactionRepo transactionRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.transactionRepo = transactionRepo;
    }

     //show cart- author: Ba Thanh
    public List<GameBasicDTO> getCart(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        List<GameBasicDTO> listCart = new ArrayList<GameBasicDTO>();
        for (Game game : user.getCartGames()) {
            GameBasicDTO gameInCart = new GameBasicDTO();
            gameInCart.setId(game.getGameId());
            gameInCart.setTitle(game.getName());
            gameInCart.setPrice(game.getPrice());
            gameInCart.setDiscountPrice(0);
            gameInCart.setOriginalPrice(gameInCart.getPrice()+gameInCart.getDiscountPrice());
            listCart.add(gameInCart);
        }
        return listCart;
    }

    //add games to cart- author: Ba Thanh
    public List<GameBasicDTO> addGameToCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
        }
        // Check if user already owns the game
        if (user.getGames() != null && user.getGames().stream().anyMatch(g -> g.getGameId().equals(gameId))) {
            throw new RuntimeException("You already own this game");
        }
        if (user.getCartGames().stream().anyMatch(g -> g.getGameId().equals(gameId))) {
            throw new RuntimeException("Game already in cart");
        }
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        user.getCartGames().add(game);
        userRepo.save(user);
        return getCart(userId);
    }

    //remove games from cart- author: Ba Thanh
    public List<GameBasicDTO> removeGameFromCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
        }
        user.getCartGames().removeIf(game -> gameId.equals(game.getGameId()));
        userRepo.save(user);
        return getCart(userId);
    }

    //calculate total price of cart- author: Ba Thanh
    public double calculateCartTotal(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        return user.getCartGames().stream().mapToDouble(Game::getPrice).sum();
    }

    //checkout- author: Ba Thanh
    public List<GameBasicDTO> checkout(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        if (user.getCartGames().isEmpty())
            throw new RuntimeException("Cart is empty");
        // Only checkout games not already owned
        java.util.Set<Game> ownedGames = user.getGames() != null ? user.getGames() : new java.util.HashSet<>();
        List<Game> gamesToBuy = user.getCartGames().stream()
            .filter(game -> ownedGames.stream().noneMatch(owned -> owned.getGameId().equals(game.getGameId())))
            .toList();
        if (gamesToBuy.isEmpty()) {
            throw new RuntimeException("All games in cart are already owned");
        }
        double total = gamesToBuy.stream().mapToDouble(Game::getPrice).sum();
        if (user.getWalletBalance() < total) {
            throw new RuntimeException("Insufficient balance");
        }
        // Lưu lại balance trước khi trừ
        double oldBalance = user.getWalletBalance();
        // Trừ tiền
        user.setWalletBalance(oldBalance - total);
        // Tạo transaction cho từng game chưa sở hữu
        for (Game game : gamesToBuy) {
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setTotalAmount(game.getPrice());
            transaction.setCreatedAt(LocalDate.now());
            // Save transaction first to generate ID
            transaction = transactionRepo.save(transaction);
            // Always create a new ArrayList for transactionDetails
            List<TransactionDetail> transactionDetails = new java.util.ArrayList<>();
            TransactionDetail detail = new TransactionDetail();
            CompositedKey key = new CompositedKey();
            key.setKey1(transaction.getTransactionId()); // Map to TransactionID
            key.setKey2(game.getGameId()); // Map to GameID
            detail.setId(key);
            detail.setTransaction(transaction);
            detail.setGame(game);
            detail.setPrice(game.getPrice());
            transactionDetails.add(detail);
            transaction.setTransactionDetail(transactionDetails);
            transactionRepo.save(transaction);
            // Add game to user's library
            ownedGames.add(game);
        }
        // Remove only the games that were just bought from cart
        user.getCartGames().removeAll(gamesToBuy);
        user.setGames(ownedGames);
        userRepo.save(user);
        return getCart(userId);
    }

   
}
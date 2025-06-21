package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.entity.CompositedKey;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.transaction.TransactionDetail;
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.TransactionRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CartService {
    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final TransactionRepo transactionRepo;
    private final UserService userService;

    @Autowired
    public CartService(UserRepo userRepo, GameRepo gameRepo, TransactionRepo transactionRepo, UserService userService) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.transactionRepo = transactionRepo;
        this.userService = userService;
    }

    // Get current user ID from security context
    private Long getCurrentUserId() {
        CustomUserDetail userDetails = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getUserId();
    }

     //show cart- author: Ba Thanh
    public CartDTO getCart() {
        Long userId = getCurrentUserId();
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new EntityNotFoundException("User not found");
        CartDTO cartDTO = new CartDTO();
        List<GameBasicDTO> listCart = new ArrayList<>();
        for (Game game : user.getCartGames()) {
            GameBasicDTO gameInCart = new GameBasicDTO();
            gameInCart.setId(game.getGameId());
            gameInCart.setTitle(game.getName());
            gameInCart.setPrice(game.getPrice());
            gameInCart.setDiscountPrice(0);
            gameInCart.setOriginalPrice(gameInCart.getPrice()+gameInCart.getDiscountPrice());
            listCart.add(gameInCart);
        }
        cartDTO.setUserId(userId);
        cartDTO.setListCart(listCart);
        return cartDTO;
    }

    //add games to cart- author: Ba Thanh
    public CartDTO addGameToCart(Long gameId) {
        Long userId = getCurrentUserId();
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new EntityNotFoundException("User not found");
        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
        }
        // Check if user already owns the game
        if (user.getGames() != null && user.getGames().stream().anyMatch(g -> g.getGameId().equals(gameId))) {
            throw new IllegalArgumentException("You already own this game");
        }
        if (user.getCartGames().stream().anyMatch(g -> g.getGameId().equals(gameId))) {
            throw new IllegalArgumentException("Game already in cart");
        }
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        user.getCartGames().add(game);
        userRepo.save(user);
        return getCart();
    }

    //remove games from cart- author: Ba Thanh
    public CartDTO removeGameFromCart(Long gameId) {
        Long userId = getCurrentUserId();
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new EntityNotFoundException("User not found");
        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
        }
        user.getCartGames().removeIf(game -> gameId.equals(game.getGameId()));
        userRepo.save(user);
        return getCart();
    }

    //calculate total price of cart- author: Ba Thanh
    public double calculateCartTotal() {
        Long userId = getCurrentUserId();
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new EntityNotFoundException("User not found");
        return user.getCartGames().stream().mapToDouble(Game::getPrice).sum();
    }

    
    public CartDTO checkout() {
        Long userId = getCurrentUserId();
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new EntityNotFoundException("User not found");
        if (user.getCartGames() == null || user.getCartGames().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        };
        double totalAmount = calculateCartTotal();

        //trừ tiền trong ví của người dùng
        userService.subtractUserBalance(totalAmount);
        
        for (Game game : user.getCartGames()) {
            // Tạo transaction cho từng game
            userService.createTransaction(game);
            // Thêm game vào library
            userService.addGameToLibrary(game);
        }
        
        // Xóa cart
        user.setCartGames(new HashSet<>());
        userRepo.save(user);
        
        return getCart();
    }
}
package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class CartService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GameRepo gameRepo;

    public CartService(UserRepo userRepo, GameRepo gameRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
    }

     //show cart- author: Ba Thanh
    public CartDTO getCart(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(userId);
        double total = 0.0;
        for (Game game : user.getCartGames()) {
            CartDTO.CartItemDTO item = new CartDTO.CartItemDTO();
            item.setGameId(game.getGameId());
            item.setGameName(game.getName());
            item.setPrice(game.getPrice());
            cartDTO.getCartItems().add(item);
            total += game.getPrice();
        }
        cartDTO.setTotal(total);
        return cartDTO;
    }

    //add games to cart- author: Ba Thanh
    public CartDTO addGameToCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
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
    public CartDTO removeGameFromCart(Long userId, Long gameId) {
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
    public CartDTO checkout(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) throw new RuntimeException("User not found");
        double total = calculateCartTotal(userId);
        if (user.getWalletBalance() < total) {
            throw new RuntimeException("Insufficient balance");
        }
        //tru account balance
        // user.setWalletBalance(user.getWalletBalance() - total);
        user.getCartGames().clear();
        userRepo.save(user);
        return getCart(userId);
    }

   
}
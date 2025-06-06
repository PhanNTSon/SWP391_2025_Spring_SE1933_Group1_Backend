package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.CartRepo;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private GameRepo gameRepo;

    @Autowired
    public CartService(CartRepo cartRepo, GameRepo gameRepo) {
        this.cartRepo = cartRepo;
        this.gameRepo = gameRepo;
    }

    //add games to cart
    public void addGameToCart(Long userId, Long gameId) {
        //Long userId = 1L; 
        User user = cartRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        if (!user.getCartGames().contains(game)) {
            user.getCartGames().add(game);
            cartRepo.save(user);
        }
    }

    //remove games from cart
    public void removeGameFromCart(Long userId, Long gameId) {
        //Long userId = 1L;
        User user = cartRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        if (user.getCartGames().contains(game)) {
            user.getCartGames().remove(game);
            cartRepo.save(user);
        }
    }

    //show games in cart
    public CartDTO showCart(Long userId) {
        //Long userId = 1L;
        User user = cartRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<Game> cartGames = user.getCartGames();
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(userId);
        cartDTO.setGames(cartGames != null? cartGames : new HashSet<>());
        return cartDTO;
    }

    public double calculateCartPrice(Long userId) {
        //Long userId = 1L; 
        User user = cartRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCartGames().stream().mapToDouble(Game::getPrice).sum();
    }
}
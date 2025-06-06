package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.service.CartService;
import com.se1933g01.steam_clone_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Author: Phan Son
 */

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final CartService cartService;

    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @Autowired
    @GetMapping("/users")
    public User getAllUsers() {
        Long userId = 1L;
        return userService.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUser(@RequestHeader(value = "userId", required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in header");
                return ResponseEntity.badRequest().body(response);
            }
            User user = userService.getUser(userId);

            response.put("success", true);
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Show games in cart
    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> showCart(@RequestHeader(value = "userId", required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in header");
                return ResponseEntity.badRequest().body(response);
            }
            CartDTO cartDTO = cartService.showCart(userId);

            response.put("success", true);
            response.put("data", cartDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Add games to cart
    @PostMapping("/cart/add")
    public ResponseEntity<Map<String, Object>> addGameToCart(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestParam Long gameId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in header");
                return ResponseEntity.badRequest().body(response);
            }
            if (gameId == null) {
                response.put("success", false);
                response.put("message", "Game ID cannot be null");
                return ResponseEntity.badRequest().body(response);
            }
            cartService.addGameToCart(userId, gameId);
            response.put("success", true);
            response.put("message", "Adding game to cart successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add game to cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Remove games from cart
    @DeleteMapping("/cart/remove")
    public ResponseEntity<Map<String, Object>> removeGameFromCart(
            @RequestHeader(value = "userId", required = false) Long userId,
            @RequestParam Long gameId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in header");
                return ResponseEntity.badRequest().body(response);
            }
            if (gameId == null) {
                response.put("success", false);
                response.put("message", "Game ID cannot be null");
                return ResponseEntity.badRequest().body(response);
            }
            cartService.removeGameFromCart(userId, gameId);
            response.put("success", true);
            response.put("message", "Deleting game from cart successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to remove game from cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Simulate checkout
    @GetMapping("/cart/checkout")
    public ResponseEntity<Map<String, Object>> calculateCartPrice(
            @RequestHeader(value = "userId", required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in header");
                return ResponseEntity.badRequest().body(response);
            }
            double totalPrice = cartService.calculateCartPrice(userId);

            response.put("success", true);
            response.put("data", totalPrice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to calculate cart price: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Show transaction history
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> showTransactions(
            @RequestHeader(value = "userId", required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in header");
                return ResponseEntity.badRequest().body(response);
            }
            List<Transaction> transactions = userService.showTransactions(userId);

            response.put("success", true);
            response.put("data", transactions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
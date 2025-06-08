package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.service.CartService;
import com.se1933g01.steam_clone_backend.service.UserService;

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
    private final UserService userService;
    private final CartService cartService;

    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    // Get all users (only userId and userName)
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userService.getAllUsers();
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getUserID());
                map.put("userName", user.getUsername());
                return map;
            }).toList();
            response.put("success", true);
            response.put("data", userList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
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

    //show carts
    @GetMapping("/{userId}/cart")
    public ResponseEntity<Map<String, Object>> showCarts(
            @PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
                return ResponseEntity.badRequest().body(response);
            }
            CartDTO cart = cartService.getCart(userId);

            response.put("success", true);
            response.put("data", cart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get carts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Add games to cart
    @GetMapping("/{userId}/cart/add")//post
    public ResponseEntity<Map<String, Object>> addGameToCart(
            @PathVariable(value = "userId") Long userId,
            @RequestParam Long gameId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
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
    @GetMapping("/{userId}/cart/remove")//delete
    public ResponseEntity<Map<String, Object>> removeGameFromCart(
            @PathVariable(value = "userId") Long userId,
            @RequestParam Long gameId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
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

    // Checkout cart
    @GetMapping("/{userId}/cart/checkout")
    public ResponseEntity<Map<String, Object>> checkoutCart(@PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
                return ResponseEntity.badRequest().body(response);
            }
            CartDTO cart = cartService.checkout(userId);
            response.put("success", true);
            response.put("message", "Checkout successfully.");
            response.put("data", cart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Checkout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Show transaction history
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<Map<String, Object>> showTransactions(
            @PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
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
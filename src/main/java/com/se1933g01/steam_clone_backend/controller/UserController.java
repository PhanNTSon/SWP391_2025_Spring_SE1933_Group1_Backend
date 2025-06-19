package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.FeedbackDTO;
import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.LibraryDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steam_clone_backend.dto.User.UserDetailDTO;
import com.se1933g01.steam_clone_backend.dto.User.UserUpdateDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.service.RequestService;
import com.se1933g01.steam_clone_backend.service.UserService;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    
    @Autowired
    private RequestService requestService;

    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    /**
     * @Author: Ba Thanh
     * UserID get in Security Context
     */
    @GetMapping("/cart")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> showCart(@PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
                return ResponseEntity.badRequest().body(response);
            }
            List<GameBasicDTO> listCart = cartService.getCart(userId).getListCart();
            response.put("success", true);
            response.put("data", listCart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Author: Ba Thanh
     */
    // Add games to cart
    @PostMapping("/cart/add")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
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

    /**
     * Author: Ba Thanh
     */
    // Remove games from cart
    @DeleteMapping("/cart/remove")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
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

    /**
     * Author: Ba Thanh
     */
    // Checkout cart
    @PostMapping("/cart/checkout")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> checkoutCart(@PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
                return ResponseEntity.badRequest().body(response);
            }
            cartService.checkout(userId);
            response.put("success", true);
            response.put("message", "Checkout successfully.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", "Checkout failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Checkout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Author: Ba Thanh
     */
    // Show transaction history (chỉ trả về các trường cần thiết)
    @GetMapping("/transaction")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> showTransactions(@PathVariable(value = "userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("success", false);
                response.put("message", "User ID is required in URL");
                return ResponseEntity.badRequest().body(response);
            }
            List<Transaction> transactions = userService.showTransactions(userId);
            List<Map<String, Object>> result = transactions.stream().map(tran -> {
                Map<String, Object> map = new HashMap<>();
                map.put("transactionId", tran.getTransactionId());
                map.put("dateCreated", tran.getCreatedAt());
                // Get game info from the first TransactionDetail (each transaction has one
                // game)
                if (tran.getTransactionDetail() != null && !tran.getTransactionDetail().isEmpty()) {
                    var detail = tran.getTransactionDetail().get(0);
                    map.put("gameId", detail.getGame() != null ? detail.getGame().getGameId() : null);
                    map.put("gameName", detail.getGame() != null ? detail.getGame().getName() : null);
                    map.put("price", detail.getPrice());
                } else {
                    map.put("gameId", null);
                    map.put("gameName", null);
                    map.put("price", null);
                }
                return map;
            }).toList();
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /* author: bathanh */
    // show library
    @GetMapping("/library")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<LibraryDTO> showLibrary(@PathVariable Long userId) {
        LibraryDTO dto = userService.showLibrary(userId);
        return ResponseEntity.ok(dto);
    }

    /* author: bathanh */
    // api check if game is in cart or not
    @GetMapping("/cart/contain/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Boolean> isGameInCart(@PathVariable Long userId, @PathVariable Long gameId) {
        return ResponseEntity.ok(userService.isGameInCart(userId, gameId));
    }

    /* author: bathanh */
    // api check if game is in library or not
    @GetMapping("/library/contain/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Boolean> isGameOwned(@PathVariable Long userId, @PathVariable Long gameId) {
        return ResponseEntity.ok(userService.isGameOwned(userId, gameId));
    }

    /* author: bathanh */
    // api get user account balance
    @GetMapping("/wallet")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Double> getUserBalance(@PathVariable Long userId) {
        Double balance = userService.getUserBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /* author: bathanh */
    // api add user account balance
    @PostMapping("/wallet/add")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Double> addBalance(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        Double newBalance = userService.addUserBalance(userId, amount);
        return ResponseEntity.ok(newBalance);
    }

    /* author: bathanh */
    // api add user account balance
    @PostMapping("/wallet/subtract")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<?> subtractBalance(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        try {
            Double newBalance = userService.subtractUserBalance(userId, amount);
            return ResponseEntity.ok(newBalance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    /**
     * Author: kerri
     * UserID get in Security Context
     * @return
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<UserDetailDTO> getUserProfile(@PathVariable Long userId) {
        UserDetailDTO userDto = userService.findUserDetailById(userId);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<UserDetailDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDetailDTO updatedUser = userService.updateUserProfile(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    
}

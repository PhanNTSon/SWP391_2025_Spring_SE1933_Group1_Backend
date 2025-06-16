package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.LibraryDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.service.CartService;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.service.GoogleDriveService;
import com.se1933g01.steam_clone_backend.service.RequestService;
import com.se1933g01.steam_clone_backend.service.UserService;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final CartService cartService;
    @Autowired
    private GoogleDriveService googleDriveService;
    @Autowired
    private RequestService requestService;
    public UserController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }
    /**
    * Author: Ba Thanh
    */
    // Get all users (only userId and userName)
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userService.getAllUsers();
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("walletBalance", user.getWalletBalance());
                map.put("userName", user.getUsername());
                map.put("userId", user.getUserID());
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
    /**
    * Author: Ba Thanh
    */
    // Show cart 
    @GetMapping("/{userId}/cart")
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
    @PostMapping("/{userId}/cart/add")
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
    @DeleteMapping("/{userId}/cart/remove")
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
    @PostMapping("/{userId}/cart/checkout")
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
    @GetMapping("/{userId}/transactions")
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

    @GetMapping("/{userId}/library")
    public ResponseEntity<LibraryDTO> showLibrary(@PathVariable Long userId) {
        LibraryDTO dto = userService.showLibrary(userId);
        return ResponseEntity.ok(dto);
    }

    

    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = googleDriveService.generateDownloadUrl(fileId);
        System.out.println(downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }

    @PostMapping("/sendpublisher")
    public ResponseEntity<Map<String, Object>> sendPublisherApplyRequest(@RequestBody PublisherApplyRequestDTO publisherApplyRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            requestService.addPublisher(publisherApplyRequestDTO, 2L);
            response.put("message", "Sending publisher apply request successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
}

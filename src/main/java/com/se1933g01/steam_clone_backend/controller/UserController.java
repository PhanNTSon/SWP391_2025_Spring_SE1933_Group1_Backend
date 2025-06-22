package com.se1933g01.steam_clone_backend.controller;

import com.se1933g01.steam_clone_backend.dto.CartDTO;
import com.se1933g01.steam_clone_backend.dto.LibraryDTO;
import com.se1933g01.steam_clone_backend.dto.User.FriendDTO;
import com.se1933g01.steam_clone_backend.dto.User.UserDetailDTO;
import com.se1933g01.steam_clone_backend.dto.User.UserUpdateDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;
import com.se1933g01.steam_clone_backend.service.AvatarService;
import com.se1933g01.steam_clone_backend.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steam_clone_backend.service.UserService;

import java.io.IOException;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    private final UserRepo userRepo;
    private final AvatarService AvatarService;

    public UserController(UserService userService, CartService cartService, UserRepo userRepo,
            AvatarService AvatarService) {
        this.userService = userService;
        this.cartService = cartService;
        this.userRepo = userRepo;
        this.AvatarService = AvatarService;
    }

    /**
     * @Author: Ba Thanh
     *          UserID get in Security Context
     */
    @GetMapping("/cart")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<CartDTO> showCart(@AuthenticationPrincipal CustomUserDetail me) {
        try {
            CartDTO cart = cartService.getCart(me.getUser().getUserId());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Author: Ba Thanh
     */
    // Add games to cart
    @PostMapping("/cart/add")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<?> addGameToCart(@AuthenticationPrincipal CustomUserDetail me, @RequestParam Long gameId) {
        try {
            CartDTO cart = cartService.addGameToCart(me.getUser().getUserId(), gameId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Author: Ba Thanh
     */
    // Remove games from cart
    @DeleteMapping("/cart/remove")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<CartDTO> removeFromCart(@AuthenticationPrincipal CustomUserDetail me,
            @RequestParam Long gameId) {
        try {
            CartDTO cart = cartService.removeGameFromCart(me.getUser().getUserId(), gameId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Author: Ba Thanh
     */
    // Checkout cart
    @PostMapping("/cart/checkout")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<CartDTO> checkoutCart(@AuthenticationPrincipal CustomUserDetail me) {
        try {
            CartDTO cart = cartService.checkout(me.getUser().getUserId());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Author: Ba Thanh
     */
    // Show transaction history (chỉ trả về các trường cần thiết)
    @GetMapping("/transaction")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> showTransaction(@AuthenticationPrincipal CustomUserDetail me) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (me == null) {
                response.put("success", false);
                response.put("message", "User authentication required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Long userId = me.getUser().getUserId(); // Assuming CustomUserDetail has getUserId()
            List<Transaction> transactions = userService.showTransactions(userId);

            List<Map<String, Object>> transactionData = transactions.stream()
                .map(transaction -> {
                    Map<String, Object> transactionMap = new HashMap<>();
                    transactionMap.put("transactionId", transaction.getTransactionId());
                    transactionMap.put("dateCreated", transaction.getCreatedAt());
                    transactionMap.put("totalAmount", transaction.getTotalAmount());

                    List<Map<String, Object>> gameDetails = transaction.getTransactionDetail().stream()
                        .map(detail -> {
                            Map<String, Object> gameMap = new HashMap<>();
                            gameMap.put("gameId", detail.getGame() != null ? detail.getGame().getGameId() : null);
                            gameMap.put("gameName", detail.getGame() != null ? detail.getGame().getName() : "Unknown");
                            gameMap.put("price", detail.getPrice());
                            return gameMap;
                        })
                        .collect(Collectors.toList());

                    transactionMap.put("games", gameDetails);
                    return transactionMap;
                })
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("message", "Transactions retrieved successfully");
            response.put("data", transactionData);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("success", false);
            response.put("message", "User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /* author: bathanh */
    // show library
    @GetMapping("/library")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<LibraryDTO> showLibrary(@AuthenticationPrincipal CustomUserDetail me) {
        try {
            LibraryDTO library = userService.showLibrary(me.getUser().getUserId());
            return ResponseEntity.ok(library);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /* author: bathanh */
    // api check if game is in cart or not
    @GetMapping("/cart/contain/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Boolean> isGameInCart(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable Long gameId) {
        try {
            boolean isGameInCart = userService.isGameInCart(me.getUser().getUserId(), gameId);
            return ResponseEntity.ok(isGameInCart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /* author: bathanh */
    // api check if game is in library or not
    @GetMapping("/library/contain/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Boolean> isGameOwned(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable Long gameId) {
        try {
            boolean isGameOwned = userService.isGameOwned(me.getUser().getUserId(), gameId);
            return ResponseEntity.ok(isGameOwned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /* author: bathanh */
    // api get user account balance
    @GetMapping("/wallet")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<BigDecimal> getUserBalance(@AuthenticationPrincipal CustomUserDetail me) {
        try {
            BigDecimal balance = userService.getUserBalance(me.getUser().getUserId());
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /* author: bathanh */
    // api add user account balance
    @PostMapping("/wallet/add")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<BigDecimal> addBalance(@AuthenticationPrincipal CustomUserDetail me,
            @RequestParam BigDecimal amount) {
        try {
            BigDecimal newBalance = userService.addUserBalance(me.getUser().getUserId(), amount);
            return ResponseEntity.ok(newBalance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /* author: bathanh */
    // api add user account balance
    @PostMapping("/wallet/subtract")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<BigDecimal> subtractBalance(@AuthenticationPrincipal CustomUserDetail me,
            @RequestParam BigDecimal amount) {
        try {
            BigDecimal newBalance = userService.subtractUserBalance(me.getUser().getUserId(), amount);
            return ResponseEntity.ok(newBalance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Author: kerri
     * UserID get in Security Context
     * 
     * @return
     */
    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<UserDetailDTO> getUserProfile(@PathVariable Long userId) {
        UserDetailDTO userDto = userService.findUserDetailById(userId);
        return ResponseEntity.ok(userDto);//
    }

    /**
     * Author: kerri
     * 
     * @return
     */
    @PutMapping("/profile/{userId}/edit/info")
    @PreAuthorize("principal.getUser().getUserId() == #userId or hasRole('ADMIN')")
    public ResponseEntity<UserDetailDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDetailDTO updatedUser = userService.updateUserProfile(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/profile/{userId}/avatar/upload")
    @PreAuthorize("principal.getUser().getUserId() == #userId or hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = userService.uploadAndSetNewAvatar(userId, file);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            // Use proper logging instead of System.out.println
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload avatar file");
        }
    }

    /**
     * @author Phan NT Son
     * @since 21-06-2025
     * @param me
     * @return
     */
    @GetMapping("/friends")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<List<FriendDTO>> getListFriend(@AuthenticationPrincipal CustomUserDetail me) {
        return ResponseEntity.ok().body(userService.getFriends(me.getUser().getUserId()));
    }

}

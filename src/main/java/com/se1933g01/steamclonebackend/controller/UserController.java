package com.se1933g01.steamclonebackend.controller;

import org.apache.tomcat.jni.Library;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.LibraryDTO;
import com.se1933g01.steamclonebackend.dto.community.FriendshipDTO;
import com.se1933g01.steamclonebackend.dto.community.InviteDTO;
import com.se1933g01.steamclonebackend.dto.user.FriendDTO;
import com.se1933g01.steamclonebackend.dto.user.UserDetailDTO;
import com.se1933g01.steamclonebackend.dto.user.UserUpdateDTO;
import com.se1933g01.steamclonebackend.entity.transaction.Transaction;
import com.se1933g01.steamclonebackend.entity.transaction.TransactionDetail;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.service.CartService;
import com.se1933g01.steamclonebackend.service.CommunityService;
import com.se1933g01.steamclonebackend.service.LibraryService;
import com.se1933g01.steamclonebackend.service.TransactionService;
import com.se1933g01.steamclonebackend.service.UserService;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CartService cartService;
    private final TransactionService transactionService; // Added by Ba Thanh 25-06
    private final LibraryService libraryService; // Added by Ba Thanh 25-06
    private final UserRepo userRepo;
    private final CommunityService communityService; // Added by Phan NT Son 23-06

    public UserController(UserService userService, CartService cartService, UserRepo userRepo,
            CommunityService communityService, TransactionService transactionService, LibraryService libraryService) {
        this.userService = userService;
        this.cartService = cartService;
        this.userRepo = userRepo;
        this.communityService = communityService;
        this.transactionService = transactionService;
        this.libraryService = libraryService;
    }

    /**
     * @Author: Ba Thanh
     *          UserID get in Security Context
     */
    @GetMapping("/cart")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> showCart(@AuthenticationPrincipal CustomUserDetail me) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = me.getUser().getUserId();
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
            @AuthenticationPrincipal CustomUserDetail me,
            @RequestParam Long gameId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = me.getUser().getUserId();
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
            @AuthenticationPrincipal CustomUserDetail me,
            @RequestParam Long gameId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = me.getUser().getUserId();
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
    public ResponseEntity<Map<String, Object>> checkoutCart(@AuthenticationPrincipal CustomUserDetail me) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = me.getUser().getUserId();
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
    public ResponseEntity<Map<String, Object>> showTransactions(@AuthenticationPrincipal CustomUserDetail me) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = me.getUser().getUserId();
            List<Transaction> transactions = transactionService.getTransactions(userId);
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

    /**
     * Author: Ba Thanh
     * Get transaction detail based on transactionId.
     */
    @GetMapping("/transaction/detail/{transactionId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> getTransactionDetailByTransactionId(
            @AuthenticationPrincipal CustomUserDetail me,
            @PathVariable Long transactionId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = me.getUser().getUserId();
            TransactionDetail detail = transactionService.getTransactionDetailByTransactionId(userId, transactionId);

            if (detail != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("Owner", detail.getTransaction().getUser().getUsername());
                data.put("transactionId", detail.getTransaction().getTransactionId());
                data.put("gameId", detail.getGame().getGameId());
                data.put("gameName", detail.getGame().getName());
                data.put("price", detail.getPrice());
                data.put("dateCreated", detail.getTransaction().getCreatedAt());
                response.put("success", true);
                response.put("data", data);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Transaction detail not found for this transactionId.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get transaction detail: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    /* author: bathanh */
    // show library
    @GetMapping("/library")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<LibraryDTO> showLibrary(@AuthenticationPrincipal CustomUserDetail me) {
        Long userId = me.getUser().getUserId();
        LibraryDTO dto = libraryService.showLibrary(userId);
        return ResponseEntity.ok(dto);
    }

    /* author: bathanh */
    // api check if game is in cart or not
    @GetMapping("/cart/contain/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Boolean> isGameInCart(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable Long gameId) {
        Long userId = me.getUser().getUserId();
        return ResponseEntity.ok(userService.isGameInCart(userId, gameId));
    }

    /* author: bathanh */
    // api check if game is in library or not
    @GetMapping("/library/contain/{gameId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Boolean> isGameOwned(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable Long gameId) {
        Long userId = me.getUser().getUserId();
        return ResponseEntity.ok(userService.isGameOwned(userId, gameId));
    }

    /* author: bathanh */
    // api get user account balance
    @GetMapping("/wallet")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<BigDecimal> getUserBalance(@AuthenticationPrincipal CustomUserDetail me) {
        Long userId = me.getUser().getUserId();
        BigDecimal balance = userService.getUserBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /* author: bathanh */
    // api add user account balance
    @PostMapping("/wallet/add")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<BigDecimal> addBalance(
            @AuthenticationPrincipal CustomUserDetail me,
            @RequestParam BigDecimal amount) {
        Long userId = me.getUser().getUserId();
        BigDecimal newBalance = userService.addUserBalance(userId, amount);
        return ResponseEntity.ok(newBalance);
    }

    /* author: bathanh */
    // api add user account balance
    @PostMapping("/wallet/subtract")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<BigDecimal> subtractBalance(@AuthenticationPrincipal CustomUserDetail me,
            @RequestParam BigDecimal amount) {
        Long userId = me.getUser().getUserId();
        BigDecimal newBalance = userService.subtractUserBalance(userId, amount);
        return ResponseEntity.ok(newBalance);
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
     *        Get list Friends of User
     * @param me - UserID
     * @return
     */
    @GetMapping("/friends")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<List<FriendDTO>> getListFriend(@AuthenticationPrincipal CustomUserDetail me) {
        return ResponseEntity.ok().body(userService.getFriends(me.getUser().getUserId()));
    }

    /**
     * @author Phan NT Son
     * @since 23-06-2025
     *        Send an Invite to a friend
     * @param receiverId
     * @param me
     * @return
     */
    @PostMapping("/sendinvite/{receiverId}")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<FriendshipDTO> sendInvite(@PathVariable(name = "receiverId") long receiverId,
            @AuthenticationPrincipal CustomUserDetail me) {
        FriendshipDTO result = communityService.sendInvite(me.getUser().getUserId(), receiverId);
        return ResponseEntity.ok(result);
    }

    /**
     * @author Phan NT Son
     * @since 23-06-2025
     *        Get a list of Pending Invite toward User
     * @param me
     * @return
     */
    @GetMapping("/pendinginvite")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<List<InviteDTO>> getInvite(@AuthenticationPrincipal CustomUserDetail me) {
        return ResponseEntity.ok(communityService.getInvite(me.getUser().getUserId()));
    }

    /**
     * @author Phan NT Son
     * @since 23-06-2025
     *        Accept an Invite
     * @param friendId
     * @param me
     * @return
     */
    @PatchMapping("/acceptinvite/{friendId}")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<FriendshipDTO> acceptInvite(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable(name = "friendId") long friendId) {
        // Accept invite
        FriendshipDTO resDto = communityService.acceptInvite(me.getUser().getUserId(), friendId);
        // Create Converstaion in DB
        communityService.createConversation(me.getUser().getUserId(), friendId);

        return ResponseEntity.ok(resDto);
    }

    /**
     * @author Phan NT Son
     * @since 23-06-2025
     *        Block an Invite
     * @param friendId
     * @param me
     * @return
     */
    @PatchMapping("/blockinvite/{targetId}")
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<FriendshipDTO> blockInvite(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable(name = "targetId") long tId) {
        // Block invite
        FriendshipDTO resDto = communityService.blockInvite(me.getUser().getUserId(), tId);

        return ResponseEntity.ok(resDto);
    }

    /**
     * @author Phan NT Son
     * @since 23-06-2025
     *        Decline an Invite
     * @param friendId
     * @param me
     * @return
     */
    @DeleteMapping({ "/declineinvite/{targetId}", "/unfriend/{targetId}" })
    @PreAuthorize("hasAnyRole('STANDARD', 'PUBLISHER','ADMIN')")
    public ResponseEntity<String> declineInvite(@AuthenticationPrincipal CustomUserDetail me,
            @PathVariable(name = "targetId") long tId) {
        // Delete invite
        communityService.deleteFriendship(me.getUser().getUserId(), tId);

        return ResponseEntity.ok("Success");
    }

    

}

package com.se1933g01.steamclonebackend.service;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.CartDTO;
import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.user.LibraryGameDTO;
import com.se1933g01.steamclonebackend.entity.CompositedKey;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.transaction.Transaction;
import com.se1933g01.steamclonebackend.entity.transaction.TransactionDetail;
import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.user.LibraryId;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.LibraryRepository;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CartService {
    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final TransactionRepo transactionRepo;
    private final SimpMessagingTemplate simp; // Added by Phan Son 28-06
    private final LibraryRepository libraryRepo;
    private final LibraryService libraryService;
    private final EmailService emailService;
    private final String SOCKET_CART_COUNT_CHANNEL = "/queue/cart.count";
    private final String SOCKET_WALLET_BALANCE_CHANNEL = "/queue/wallet.balance";
    private final String SOCKET_LIBRARY_CHANNEL = "/queue/libraryItem.added";

    public CartService(UserRepo userRepo, GameRepo gameRepo, TransactionRepo transactionRepo,
            SimpMessagingTemplate simp, LibraryRepository libraryRepo, LibraryService libraryService, EmailService emailService) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.transactionRepo = transactionRepo;
        this.simp = simp;
        this.libraryRepo = libraryRepo;
        this.emailService = emailService;
        this.libraryService = libraryService;
    }

    /**
     * @author phan nt son
     * @param userId
     * @return numbers of games in cart of user
     * @since 05-7-2025
     */
    public long getTotalGamesInCart(long userId) {
        return this.getCart(userId).getListCart().size();
    }

    // show cart- author: Ba Thanh
    public CartDTO getCart(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null)
            throw new EntityNotFoundException("User not found");
        CartDTO cartDTO = new CartDTO();
        List<GameBasicDTO> listCart = new ArrayList<>();
        for (Game game : user.getCartGames()) {
            GameBasicDTO gameInCart = new GameBasicDTO();
            gameInCart.setId(game.getGameId());
            gameInCart.setTitle(game.getName());
            gameInCart.setImageUrl(
                    game.getMedia() != null && !game.getMedia().isEmpty() ? game.getMedia().get(0).getUrl() : null);
            gameInCart.setPrice(game.getPrice());
            gameInCart.setDiscountPrice(BigDecimal.ZERO); // Changed by Pha Son 21-06
            gameInCart.setOriginalPrice(gameInCart.getPrice().add(gameInCart.getDiscountPrice())); // Changed by Pha Son
                                                                                                   // 21-06
            listCart.add(gameInCart);
        }
        cartDTO.setUserId(userId);
        cartDTO.setListCart(listCart);
        return cartDTO;
    }

    @Transactional
    // add games to cart- author: Ba Thanh
    public CartDTO addGameToCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null)
            throw new EntityNotFoundException("User not found");

        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
        }

        // Check if user already owns the game
        // Fixed by Phan Son 2-7
        if (user.getLibraryGames() != null
                && user.getLibraryGames().stream()
                        .anyMatch(g -> g.getGame().getGameId().equals(gameId))) {
            throw new IllegalArgumentException("You already own this game");
        }
        // --!!

        if (user.getCartGames().stream().anyMatch(g -> g.getGameId().equals(gameId))) {
            throw new IllegalArgumentException("Game already in cart");
        }
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));
        user.getCartGames().add(game);
        userRepo.save(user);

        // Added by Phan Son 28-06
        CartDTO result = getCart(userId);
        simp.convertAndSendToUser(user.getUsername(), SOCKET_CART_COUNT_CHANNEL, result.getListCart().size());
        // --!!

        return result;
    }

    @Transactional
    // remove games from cart- author: Ba Thanh
    public CartDTO removeGameFromCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null)
            throw new EntityNotFoundException("User not found");
        if (user.getCartGames() == null) {
            user.setCartGames(new HashSet<>());
        }
        user.getCartGames().removeIf(game -> gameId.equals(game.getGameId()));
        userRepo.save(user);

        // Added by Phan Son 28-06
        CartDTO result = getCart(userId);
        simp.convertAndSendToUser(user.getUsername(), SOCKET_CART_COUNT_CHANNEL, result.getListCart().size());
        // --!!

        return result;
    }

    // calculate total price of cart- author: Ba Thanh
    public BigDecimal calculateCartTotal(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null)
            throw new EntityNotFoundException("User not found");
        /**
         * Changed by Phan Son 21-06
         */
        BigDecimal result = BigDecimal.ZERO;
        for (Game game : user.getCartGames()) {
            result = result.add(game.getPrice());
        }
        return result;
        // --!!
    }

    @Transactional
    // checkout- author: Ba Thanh
    public CartDTO checkout(Long userId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null)
            throw new EntityNotFoundException("User not found");
        if (user.getCartGames().isEmpty())
            throw new EntityNotFoundException("Cart is empty");

        // Only checkout games not already owned
        // Fixed by Phan son 2-7
        java.util.Set<Library> ownedGames = user.getLibraryGames() != null ? user.getLibraryGames()
                : new java.util.HashSet<>();
        List<Game> gamesToBuy = user.getCartGames().stream()
                .filter(game -> ownedGames.stream()
                        .noneMatch(owned -> owned.getGame().getGameId().equals(game.getGameId())))
                .toList();
        // --!!

        if (gamesToBuy.isEmpty()) {
            throw new RuntimeException("All games in cart are already owned");
        }
        BigDecimal total = gamesToBuy.stream().map(game -> game.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (user.getWalletBalance().compareTo(total) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        // Lưu lại balance trước khi trừ
        BigDecimal oldBalance = user.getWalletBalance();
        // Trừ tiền
        user.setWalletBalance(oldBalance.subtract(total));
        // Tạo transaction cho từng game chưa sở hữu

        for (int i = 0; i < gamesToBuy.size(); i++) {
            Game game = gamesToBuy.get(i);
            Publisher pub = game.getPublisher();
            User pubUser = pub.getUser();
            pubUser.setWalletBalance(pubUser.getWalletBalance().add(game.getPrice()));
            pub.setUser(pubUser);
            Transaction transactionpub = new Transaction();
            transactionpub.setUser(pubUser);
            transactionpub.setTotalAmount(game.getPrice());
            transactionpub.setCreatedAt(LocalDate.now());
            transactionpub.setType("Add");
            transactionRepo.save(transactionpub);

            TransactionDetail detailpub = new TransactionDetail();
            CompositedKey keypub = new CompositedKey();
            keypub.setKey1(transactionpub.getTransactionId());
            keypub.setKey2(game.getGameId());
            detailpub.setId(keypub);
            detailpub.setTransaction(transactionpub);
            detailpub.setGame(game);
            detailpub.setPrice(game.getPrice());

            List<TransactionDetail> detailspub = new ArrayList<>();
            detailspub.add(detailpub);
            transactionpub.setTransactionDetail(detailspub);
            transactionRepo.save(transactionpub);

            game.setTotalPurchased(game.getTotalPurchased() + 1);

            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setTotalAmount(game.getPrice());
            transaction.setCreatedAt(LocalDate.now());
            transaction.setType("Subtract");

            // Save transaction first to get transactionId
            transaction = transactionRepo.save(transaction);

            // Create detail
            TransactionDetail detail = new TransactionDetail();
            CompositedKey key = new CompositedKey();
            key.setKey1(transaction.getTransactionId());
            key.setKey2(game.getGameId());
            detail.setId(key);
            detail.setTransaction(transaction);
            detail.setGame(game);
            detail.setPrice(game.getPrice());

            // Set detail and save again
            List<TransactionDetail> details = new ArrayList<>();
            details.add(detail);
            transaction.setTransactionDetail(details);
            transactionRepo.save(transaction);

            // Update game data
            game.setTotalPurchased(game.getTotalPurchased() + 1);

            // Fixed by Phan Son 2-7
            Library libraryItem = new Library();
            LibraryId libraryId = new LibraryId();
            libraryId.setGameId(game.getGameId());
            libraryId.setUserId(user.getUserId());

            libraryItem.setId(libraryId);
            libraryItem.setGame(game);
            libraryItem.setUser(user);
            libraryItem.setDateAdded(LocalDateTime.now());

            user.getLibraryGames().add(libraryItem);
            libraryRepo.save(libraryItem);

            // Added by Phan son 11-07
            LibraryGameDTO newGame = libraryService.mapLibraryEntryToDto(libraryItem);
            simp.convertAndSendToUser(user.getUsername(), SOCKET_LIBRARY_CHANNEL, newGame);
            // --!!
        }
        if (gamesToBuy.size() == 1) {
            // Send email for single game purchase
            Game purchasedGame = gamesToBuy.get(0);
            emailService.sendPurchaseInvoiceEmail(user.getEmail(), user.getUserId().toString(), user.getUsername(),
                    purchasedGame.getName(), purchasedGame.getPrice());
        } else {
            // Send email for multiple game purchase
            emailService.sendMultiGameInvoiceEmail(user.getEmail(), user.getUserId().toString(), user.getUsername(),
                    gamesToBuy);
            
        }
        // Remove only the games that were just bought from cart
        user.getCartGames().removeAll(gamesToBuy);
        userRepo.save(user);

        // Added by Phan Son 30-06
        CartDTO result = getCart(userId);
        simp.convertAndSendToUser(user.getUsername(), SOCKET_CART_COUNT_CHANNEL, result.getListCart().size());
        simp.convertAndSendToUser(user.getUsername(), SOCKET_WALLET_BALANCE_CHANNEL, user.getWalletBalance());
        // --!!



        return result;
    }

    @Transactional
    public void refund(Long transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId).orElse(null);
        if (transaction == null)
            throw new EntityNotFoundException("Transaction not found");

        User user = transaction.getUser();
        if (user == null)
            throw new EntityNotFoundException("Transaction has no user");

        List<TransactionDetail> details = transaction.getTransactionDetail();
        if (details == null || details.isEmpty())
            throw new RuntimeException("Transaction has no details to refund");

        for (TransactionDetail detail : details) {
            Game game = detail.getGame();
            if (game == null)
                continue;

            BigDecimal refundAmount = detail.getPrice();

            // Hoàn tiền cho user
            user.setWalletBalance(user.getWalletBalance().add(refundAmount));

            // Trừ tiền của publisher
            Publisher publisher = game.getPublisher();
            if (publisher != null && publisher.getUser() != null) {
                User publisherUser = publisher.getUser();
                publisherUser.setWalletBalance(publisherUser.getWalletBalance().subtract(refundAmount));
                userRepo.save(publisherUser);

                // Giao dịch refund cho publisher
                Transaction pubRefund = new Transaction();
                pubRefund.setUser(publisherUser);
                pubRefund.setTotalAmount(refundAmount);
                pubRefund.setCreatedAt(LocalDate.now());
                pubRefund.setType("Refund Subtract");

                TransactionDetail pubDetail = new TransactionDetail();
                CompositedKey pubKey = new CompositedKey();
                pubKey.setKey1(pubRefund.getTransactionId());
                pubKey.setKey2(game.getGameId());
                pubDetail.setId(pubKey);
                pubDetail.setTransaction(pubRefund);
                pubDetail.setGame(game);
                pubDetail.setPrice(refundAmount);

                // pubRefund.getTransactionDetail().add(pubDetail);
                pubRefund.setTransactionDetail(new ArrayList<>(List.of(pubDetail)));

                transactionRepo.save(pubRefund);
            }

            // Giao dịch hoàn tiền cho user
            Transaction userRefund = new Transaction();
            userRefund.setUser(user);
            userRefund.setTotalAmount(refundAmount);
            userRefund.setCreatedAt(LocalDate.now());
            userRefund.setType("Refund Add");

            TransactionDetail userDetail = new TransactionDetail();
            CompositedKey userKey = new CompositedKey();
            userKey.setKey1(userRefund.getTransactionId());
            userKey.setKey2(game.getGameId());
            userDetail.setId(userKey);
            userDetail.setTransaction(userRefund);
            userDetail.setGame(game);
            userDetail.setPrice(refundAmount);

            // userRefund.getTransactionDetail().add(userDetail);
            userRefund.setTransactionDetail(new ArrayList<>(List.of(userDetail)));
            transactionRepo.save(userRefund);

            // Xóa khỏi thư viện
            LibraryId libId = new LibraryId();
            libId.setGameId(game.getGameId());
            libId.setUserId(user.getUserId());
            libraryRepo.deleteById(libId);

            // Giảm số lượt mua
            game.setTotalPurchased(game.getTotalPurchased() - 1);
        }

        userRepo.save(user);

        simp.convertAndSendToUser(user.getUsername(), SOCKET_WALLET_BALANCE_CHANNEL, user.getWalletBalance());
        simp.convertAndSendToUser(user.getUsername(), SOCKET_LIBRARY_CHANNEL, "REFUND_DONE");
    }

}
package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.dto.LibraryDTO;
import com.se1933g01.steam_clone_backend.dto.MediaDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherBasicDTO;
import com.se1933g01.steam_clone_backend.dto.TagDTO;
import com.se1933g01.steam_clone_backend.dto.User.FriendDTO;
import com.se1933g01.steam_clone_backend.dto.User.UserDetailDTO;
import com.se1933g01.steam_clone_backend.dto.User.UserUpdateDTO;
import com.se1933g01.steam_clone_backend.entity.community.Friendship;
import com.se1933g01.steam_clone_backend.entity.CompositedKey;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.dto.BannedUserDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.transaction.TransactionDetail;
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;
import com.se1933g01.steam_clone_backend.mapper.EntityMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import com.se1933g01.steam_clone_backend.repository.FriendshipRepo;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.TransactionRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final CloudinaryService CloudinaryService;

    private UserRepo userRepo;
    private TransactionRepo transactionRepo;
    private final GameRepo gameRepo;
    private EntityMapper entityMapper;
    private final FriendshipRepo friendshipRepo; // Added by Phan NT Son 21-06-2025

    private static final String USER_NOT_FOUND_MSG = "User not found";

    public UserService(UserRepo userRepo, TransactionRepo transactionRepo, EntityMapper entityMapper,
            FriendshipRepo friendshipRepo, CloudinaryService CloudinaryService, GameRepo gameRepo) {
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.gameRepo = gameRepo;
        this.entityMapper = entityMapper;
        this.friendshipRepo = friendshipRepo;
        this.CloudinaryService  = CloudinaryService;
    }

    public User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
    }

    // String constant for user not found message

    // author: Ba Thanh
    // Get userId from SecurityContextHolder

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Author: Ba Thanh
     * // Show all transactions of a user.
     */
    public List<Transaction> showTransactions(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        return transactionRepo.findByUser(user);
    }

    public Transaction createTransaction(User user, Game game) {
        // Create new transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTotalAmount(game.getPrice());
        transaction.setCreatedAt(LocalDate.now());

        // Save transaction to generate ID
        transaction = transactionRepo.save(transaction);

        // Create transaction detail
        List<TransactionDetail> transactionDetails = new ArrayList<>();
        TransactionDetail detail = new TransactionDetail();
        CompositedKey key = new CompositedKey();
        key.setKey1(transaction.getTransactionId());
        key.setKey2(game.getGameId());
        detail.setId(key);
        detail.setTransaction(transaction);
        detail.setGame(game);
        detail.setPrice(game.getPrice());
        transactionDetails.add(detail);

        // Set transaction details and save
        transaction.setTransactionDetail(transactionDetails);
        game.setTotalPurchased(game.getTotalPurchased() + 1); // Increment total purchased count
        gameRepo.save(game); // Save the game to update total purchased count
        return transactionRepo.save(transaction);
    }

    // Thêm game vào library - author: Ba Thanh
    public void addGameToLibrary(Long userId, Game game) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        java.util.Set<Game> ownedGames = user.getGames() != null
                ? user.getGames()
                : new java.util.HashSet<>();
        ownedGames.add(game);
        user.setGames(ownedGames);
        userRepo.save(user);
    }

    /**
     * Show library of a user.
     * 
     * @author Ba Thanh
     * @param userId
     * @return LibraryDTO containing user's library
     */
    public LibraryDTO showLibrary(Long userId) {
        User user = userRepo.findByIdWithLibraryGames(userId);
        if (user == null)
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG);
        LibraryDTO libraryDTO = new LibraryDTO();
        List<GameDetailDTO> gameDetailList = new ArrayList<>();
        for (Game game : user.getGames()) {
            GameDetailDTO gameInLibrary = new GameDetailDTO();
            // add game detail
            gameInLibrary.setGameId(game.getGameId());
            gameInLibrary.setPublisher(new PublisherBasicDTO(game.getPublisher().getPublisherId(),
                    game.getPublisher().getPublisherName()));
            gameInLibrary.setName(game.getName());
            gameInLibrary.setReleaseDate(game.getReleaseDate());
            gameInLibrary.setState(game.getState());
            gameInLibrary.setPrice(game.getPrice());
            gameInLibrary.setShortDescription(game.getShortDescription());
            gameInLibrary.setFullDescription(game.getFullDescription());
            gameInLibrary.setTotalPurchased(game.getTotalPurchased());
            gameInLibrary.setTags(game.getTags().stream()
                    .map(tag -> new TagDTO(tag.getTagId(), tag.getTagName()))
                    .collect(Collectors.toSet()));
            gameInLibrary.setMedia(game.getMedia().stream()
                    .map(media -> new MediaDTO(media.getMediaId(), media.getUrl(), media.getType()))
                    .toList());
            gameInLibrary.setOs(game.getOs());
            gameInLibrary.setStorage(game.getStorage());
            gameInLibrary.setProcessor(game.getProcessor());
            gameInLibrary.setMemory(game.getMemory());
            gameInLibrary.setAdditionalNotes(game.getAdditionalNotes());
            gameInLibrary.setGraphics(game.getGraphics());
            // add gamedetail into list
            gameDetailList.add(gameInLibrary);
        }
        libraryDTO.setUserId(userId);
        libraryDTO.setLibrary(gameDetailList);
        return libraryDTO;
    }

    /*
     * author: bathanh
     * check if game is in cart
     * return true if game is in cart, false otherwise.
     */
    public boolean isGameInCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null)
            return false;
        return user.getCartGames().stream().anyMatch(game -> game.getGameId().equals(gameId));
    }

    /*
     * author: bathanh
     * check if game is in library
     * return true if game is in library, false otherwise.
     */
    public boolean isGameOwned(Long userId, Long gameId) {
        User user = userRepo.findByIdWithLibraryGames(userId);
        if (user == null)
            return false;
        return user.getGames().stream().anyMatch(game -> game.getGameId().equals(gameId));
    }

    /* author: bathanh */
    // get user's balance
    public BigDecimal getUserBalance(Long userId) { // Changed by Phan Son 21-06
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        return user.getWalletBalance();
    }

    /* author: bathanh */
    // add money to user's wallet
    @Transactional
    public BigDecimal addUserBalance(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) // Changed by Phan Son 21-06
            throw new IllegalArgumentException("Amount must be positive");
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        user.setWalletBalance(user.getWalletBalance().add(amount)); // Changed by Phan Son 21-06
        userRepo.save(user);
        return user.getWalletBalance();
    }

    /* author: bathanh */
    // deduct user's wallet
    @Transactional
    public BigDecimal subtractUserBalance(Long userId, BigDecimal amount) { // Changed by Phan Son 21-06
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount must be positive");
        User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        if (user.getWalletBalance().compareTo(amount) < 0) { // Changed by Phan Son 21-06
            throw new IllegalArgumentException("Insufficient balance");
        }
        user.setWalletBalance(user.getWalletBalance().subtract(amount)); // Changed by Phan Son 21-06
        userRepo.save(user);
        return user.getWalletBalance();
    }

    /**
     * Get an User by username.
     * 
     * @author Phan NT Son
     * @param username
     * @return an User
     * @since 11-06-2025
     */
    public User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MSG));
    }

    /**
     * @author Phan NT Son
     * @return
     * @since 13-06-2025
     */
    public Page<BannedUserDTO> getAllBannedUser(int page) {
        PageRequest pageable = PageRequest.of(page, 10, Sort.by("username").descending());
        return userRepo.findAllByBannedStatus(pageable).map(u -> new BannedUserDTO(
                u.getUserId(),
                u.getUsername(),
                u.getRole().getRoleName()));
    }

    /**
     * @author kerri
     * @param userId
     * @param userUpdateDTO
     * @return
     */
    public UserDetailDTO updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (userUpdateDTO.getProfileName() != null) {
            user.setProfileName(userUpdateDTO.getProfileName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getCountry() != null) {
            user.setCountry(userUpdateDTO.getCountry());
        }
        if (userUpdateDTO.getDob() != null) {
            user.setDob(userUpdateDTO.getDob());
        }
        if (userUpdateDTO.getGender() != null) {
            user.setGender(userUpdateDTO.getGender());
        }
        if (userUpdateDTO.getSummary() != null) {
            user.setSummary(userUpdateDTO.getSummary());
        }
        User savedUser = userRepo.save(user);
        return entityMapper.toUserDetailDTO(savedUser);
    }

    /**
     * @author kerri
     * @param userId
     * @return
     */
    public UserDetailDTO findUserDetailById(Long userId) {
        return userRepo.findById(userId).map(user -> new UserDetailDTO(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getWalletBalance(),
                user.getAvatarUrl(),
                user.getCountry(),
                user.getDob(),
                user.getGender(),
                user.getProfileName(),
                user.getSummary(),
                user.isBanStatus()))
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MSG));
    }

    @Transactional 
    public String uploadAndSetNewAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        String fileUrl = CloudinaryService.uploadFile(file);
        user.setAvatarUrl(fileUrl);
        userRepo.save(user);

        return fileUrl;
    }

    /**
     * @author Phan NT Son
     * @since 21-06-2025
     * 
     *        Get friends list base on UserID
     * 
     * @param userId
     * @return
     */
    public List<FriendDTO> getFriends(Long userId) {
        List<Friendship> queryResultList = friendshipRepo.findAllByUserId(userId);
        List<FriendDTO> mappingResult = queryResultList.stream()
                .map(friendship -> new FriendDTO(
                        friendship.getFriend().getUserId(),
                        friendship.getFriend().getUsername(),
                        friendship.getFriend().getAvatarUrl()))
                .collect(Collectors.toList());
        return mappingResult;
    }
}
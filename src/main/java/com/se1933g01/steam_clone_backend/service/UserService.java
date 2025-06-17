package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.dto.LibraryDTO;
import com.se1933g01.steam_clone_backend.dto.MediaDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherBasicDTO;
import com.se1933g01.steam_clone_backend.dto.TagDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.dto.BannedUserDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import jakarta.transaction.Transactional;

import com.se1933g01.steam_clone_backend.repository.TransactionRepo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TransactionRepo transactionRepo;

    public User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
    * Author: Ba Thanh
    // Show all transactions of a user.
    */
    public List<Transaction> showTransactions(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepo.findByUser(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
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
        if (user == null) throw new RuntimeException("User not found");
        LibraryDTO libraryDTO = new LibraryDTO();
        List<GameDetailDTO> gameDetailList = new ArrayList<>();
        for (Game game : user.getGames()) {
            GameDetailDTO gameInLibrary = new GameDetailDTO();
            // add game detail
            gameInLibrary.setGameId(game.getGameId());
            gameInLibrary.setPublisher(new PublisherBasicDTO(game.getPublisher().getPublisherId(), game.getPublisher().getPublisherName()));
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
                .collect(Collectors.toList()));
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

    /*author: bathanh 
     * check if game is in cart
     * return true if game is in cart, false otherwise.
    */
    public boolean isGameInCart(Long userId, Long gameId) {
        User user = userRepo.findByIdWithCartGames(userId);
        if (user == null) return false;
        return user.getCartGames().stream().anyMatch(game -> game.getGameId().equals(gameId));
    }
    /*author: bathanh 
     * check if game is in library
     * return true if game is in library, false otherwise.
    */
    public boolean isGameOwned(Long userId, Long gameId) {
        User user = userRepo.findByIdWithLibraryGames(userId);
        if (user == null) return false;
        return user.getGames().stream().anyMatch(game -> game.getGameId().equals(gameId));
    }

    /*author: bathanh */
    //get user's balance
    public double getUserBalance(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWalletBalance();
    }

    /*author: bathanh */
    //add money to user's wallet
    @Transactional
    public Double addUserBalance(Long userId, Double amount) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setWalletBalance(user.getWalletBalance() + amount);
        userRepo.save(user);
        return user.getWalletBalance();
    }

    /*author: bathanh */
    //deduct user's wallet
    @Transactional
    public Double subtractUserBalance(Long userId, Double amount) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getWalletBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        user.setWalletBalance(user.getWalletBalance() - amount);
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * @author Phan NT Son
     * @return
     * @since 13-06-2025
     */
    public Page<BannedUserDTO> getAllBannedUser(int page) {
        PageRequest pageable = PageRequest.of(page, 10, Sort.by("username").descending());
        return userRepo.findAllByBannedStatus(pageable).map(u -> new BannedUserDTO(
                u.getUserID(),
                u.getUsername(),
                u.getRole().getRoleName()));
    }
}
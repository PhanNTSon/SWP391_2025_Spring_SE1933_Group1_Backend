package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.BannedUserDTO;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;
import com.se1933g01.steam_clone_backend.repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
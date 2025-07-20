package com.se1933g01.steamclonebackend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steamclonebackend.dto.BannedUserDTO;
import com.se1933g01.steamclonebackend.dto.user.FriendDTO;
import com.se1933g01.steamclonebackend.dto.user.UserDetailDTO;
import com.se1933g01.steamclonebackend.dto.user.UserUpdateDTO;
import com.se1933g01.steamclonebackend.entity.community.Friendship;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.mapper.EntityMapper;
import com.se1933g01.steamclonebackend.repository.FriendshipRepo;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;


@Service
public class UserService {

    private final CloudinaryService CloudinaryService;

    private UserRepo userRepo;
    private TransactionRepo transactionRepo;
    private final FriendshipRepo friendshipRepo; // Added by Phan NT Son 21-06-2025

    private EmailService emailService;
    private ModelMapper modelMapper;

    private static final String USER_NOT_FOUND_MSG = "User not found";

    public UserService(UserRepo userRepo, TransactionRepo transactionRepo,
            FriendshipRepo friendshipRepo, CloudinaryService cloudinaryService, EmailService emailService, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.friendshipRepo = friendshipRepo;
        this.CloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
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
        User user = userRepo.findById(userId).orElse(null);
        if (user == null)
            return false;

        // fixed by Phan Son 2-7
        return user.getLibraryGames().stream()
                .anyMatch(lib -> lib.getGame().getGameId().equals(gameId));
        // --!!
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
    public Page<UserDetailDTO> getAllActiveUser(String name,Pageable pageable) {

        Page<User> requestUser;
        if (name != null && !name.trim().isEmpty()) {
            requestUser = userRepo.findAllByUsernameContainingIgnoreCaseAndBannedStatusFalse(name, pageable);
        } else {
            requestUser = userRepo.findAllByBannedStatusFalse(pageable);
        }

        return requestUser.map(user -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserDetailDTO dto = modelMapper.map(user, UserDetailDTO.class);
            dto.setAvatarUrl(user.getAvatarUrl());
            return dto;
        });
    }
    public Page<UserDetailDTO> getAllBannedUser(String name,Pageable pageable) {

        Page<User> requestUser;
        if (name != null && !name.trim().isEmpty()) {
            requestUser = userRepo.findAllByUsernameContainingIgnoreCaseAndBannedStatusTrue(name, pageable);
        } else {
            requestUser = userRepo.findAllByBannedStatusTrue(pageable);
        }

        return requestUser.map(user -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            UserDetailDTO dto = modelMapper.map(user, UserDetailDTO.class);
            dto.setAvatarUrl(user.getAvatarUrl());
            return dto;
        });
    }
    public void banUser(Long userId){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        user.setBanStatus(true);
        userRepo.save(user);
    }

    public void unbanUser(Long userId){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
        user.setBanStatus(false);
        userRepo.save(user);
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
        return EntityMapper.toUserDetailDTO(savedUser);
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
        List<Friendship> queryResultList = friendshipRepo.findAllFriend(userId);
        List<FriendDTO> mappingResult = queryResultList.stream()
                .map(friendship -> new FriendDTO(
                        friendship.getFriend().getUserId(),
                        friendship.getFriend().getUsername(),
                        friendship.getFriend().getAvatarUrl()))
                .collect(Collectors.toList());
        return mappingResult;
    }

    /**
     * @author Phan NT Son
     * @since 24-06-2025
     * 
     *        Get list of Blocked users
     * 
     * @param userId
     * @return
     */
    public List<FriendDTO> getBlocked(Long userId) {
        List<Friendship> queryResultList = friendshipRepo.findAllBlocked(userId);
        List<FriendDTO> mappingResult = queryResultList.stream()
                .map(friendship -> new FriendDTO(
                        friendship.getFriend().getUserId(),
                        friendship.getFriend().getUsername(),
                        friendship.getFriend().getAvatarUrl()))
                .collect(Collectors.toList());
        return mappingResult;
    }

    @Transactional
    public void requestEmailChange(Long userId, String newEmail) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kiểm tra xem email mới có đang được sử dụng bởi người khác không
        if (userRepo.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("New email address is already in use.");
        }

        String token = String.format("%06d", new Random().nextInt(999999));

        // Lưu thông tin yêu cầu vào user
        user.setNewEmailAddress(newEmail);
        user.setEmailChangeToken(token);
        user.setEmailChangeTokenExpiry(LocalDateTime.now().plusMinutes(10)); // Hết hạn sau 10 phút

        userRepo.save(user);

        // Gửi email chứa mã đến địa chỉ email CŨ
        String emailText = "Hello " + user.getUsername() + ",\n\n"
                + "You have requested to change your email address.\n"
                + "Your verification code is: " + token + "\n\n"
                + "This code will expire in 10 minutes.\n\n"
                + "If you did not request this change, please secure your account.";

        emailService.sendEmail(user.getEmail(), "Email Change Verification Code", emailText);
    }

    @Transactional
    public void confirmEmailChange(Long userId, String token, String newEmail) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Kiểm tra token và email có khớp với yêu cầu đã lưu không
        if (user.getEmailChangeToken() == null || !user.getEmailChangeToken().equals(token) ||
                user.getNewEmailAddress() == null || !user.getNewEmailAddress().equals(newEmail)) {
            throw new IllegalArgumentException("Invalid verification code or email address.");
        }

        // Kiểm tra token hết hạn
        if (user.getEmailChangeTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code has expired.");
        }

        // Mọi thứ hợp lệ, cập nhật email mới
        user.setEmail(user.getNewEmailAddress());

        // Xóa thông tin yêu cầu thay đổi
        user.setNewEmailAddress(null);
        user.setEmailChangeToken(null);
        user.setEmailChangeTokenExpiry(null);

        userRepo.save(user);
    }
}
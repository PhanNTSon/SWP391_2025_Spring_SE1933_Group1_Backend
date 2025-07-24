package com.se1933g01.steamclonebackend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.user.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cartGames WHERE u.userId = :userId")
    User findByIdWithCartGames(@Param("userId") Long userId);
    
    boolean existsByUsername(String username); //added by Loc Phan

    boolean existsByEmail(String email);
    

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndEmail(String username, String email);
    /**
     * @author Phan NT Son
     * @return List of User
     * @since 13-06-2025
     */
    @Query("SELECT u FROM User u WHERE u.banStatus = true")
    Page<User> findAllByBannedStatusTrue(Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.banStatus = false")
    Page<User> findAllByBannedStatusFalse(Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% AND u.banStatus = false")
    Page<User> findAllByUsernameContainingIgnoreCaseAndBannedStatusFalse(String username, Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username% AND u.banStatus = true")
    Page<User> findAllByUsernameContainingIgnoreCaseAndBannedStatusTrue(String username, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM \"User\"", nativeQuery = true)
    Long countAllUsers();

}

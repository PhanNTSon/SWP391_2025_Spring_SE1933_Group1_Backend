package com.se1933g01.steam_clone_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.user.User;
@Repository
public interface UserRepo extends JpaRepository<User,Long>{
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cartGames WHERE u.userID = :userId")
    User findByIdWithCartGames(@Param("userId") Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.games WHERE u.userID = :userId")
    User findByIdWithLibraryGames(@Param("userId") Long userId);


    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    /**
     * @author Phan NT Son
     * @return List of User
     * @since 13-06-2025
     */
    @Query("SELECT u FROM User u WHERE u.banStatus = true")
    Page<User> findAllByBannedStatus(Pageable pageable);
}

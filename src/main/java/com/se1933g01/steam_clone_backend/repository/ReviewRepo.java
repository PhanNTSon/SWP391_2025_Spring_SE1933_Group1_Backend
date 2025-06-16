package com.se1933g01.steam_clone_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.entity.game.ReviewKey;

/**
 * Author: Phan Son
 */
public interface ReviewRepo extends JpaRepository<Review, ReviewKey> {
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.game.gameId = :gameId")
    List<Review> findByGame_GameId(@Param("gameId") Long gameId);

    @Query(value = "SELECT COUNT(*) FROM ReviewHelpful WHERE ReviewGameID = :gameId AND ReviewUserID = :userId", nativeQuery = true)
    long countLikedByUsers(@Param("gameId") Long gameId, @Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM ReviewNotHelpful WHERE ReviewGameID = :gameId AND ReviewUserID = :userId", nativeQuery = true)
    long countUnLikedByUsers(@Param("gameId") Long gameId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM ReviewHelpful WHERE ReviewGameID = :gameId AND ReviewUserID = :userId", nativeQuery = true)
    int deleteAllLiked(@Param("gameId") Long gameId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM ReviewNotHelpful WHERE ReviewGameID = :gameId AND ReviewUserID = :userId", nativeQuery = true)
    int deleteAllUnLiked(@Param("gameId") Long gameId, @Param("userId") Long userId);
}

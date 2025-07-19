package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.Friendship;
import com.se1933g01.steamclonebackend.entity.community.FriendshipId;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Repository
public interface FriendshipRepo extends JpaRepository<Friendship, FriendshipId> {

    @Query("SELECT f FROM Friendship f WHERE f.user1.userId = :userId OR f.user2.userId = :userId ")
    Optional<List<Friendship>> findAllFriendship(@Param("userId") Long userId);

    // Assume that user1Id < user2Id
    @Query("SELECT f FROM Friendship f WHERE f.user1.userId = :user1Id AND f.user2.userId = :user2Id ")
    Optional<Friendship> findByUser1AndUser2(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

}

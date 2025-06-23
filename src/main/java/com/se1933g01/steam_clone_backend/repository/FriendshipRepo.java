package com.se1933g01.steam_clone_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.community.Friendship;
import com.se1933g01.steam_clone_backend.entity.community.FriendshipId;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Repository
public interface FriendshipRepo extends JpaRepository<Friendship, FriendshipId> {

    @Query("SELECT f FROM Friendship f WHERE f.id.userId = :userId")
    List<Friendship> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE f.id.userId = :userId AND f.status = 'Accepted'")
    List<Friendship> findAllFriend(@Param("userId") Long userId);

    @Query("SELECT f FROM Friendship f WHERE f.id.friendId = :userId AND f.status = 'Pending'")
    List<Friendship> findAllInvite(@Param("userId") long userId);
}

package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.FriendRequest;

@Repository
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT f FROM FriendRequest f WHERE f.sender.userId = :senderId")
    Optional<List<FriendRequest>> findAllBySenderId(@Param("senderId") Long senderId);

    @Query("SELECT f FROM FriendRequest f WHERE f.receiver.userId = :receiverId")
    Optional<List<FriendRequest>> findAllByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT f FROM FriendRequest f WHERE f.receiver.userId = :receiverId AND f.sender.userId = :senderId")
    Optional<FriendRequest> findBySenderIdAndReceiverId(@Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId);

}

package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.Conversation;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Repository
public interface ConversationRepo extends JpaRepository<Conversation, Long> {

    /**
     * Query to get a Conversation. Assume that User1ID < User2ID
     * @param user1Id
     * @param user2Id
     * @return
     */
    @Query("SELECT c FROM Conversation c WHERE c.user1.userId =: user1Id AND c.user2.userId =: user2Id")
    Conversation findByUser1AndUser2(@Param("user1Id") long user1Id, @Param("user2Id") long user2Id);
}

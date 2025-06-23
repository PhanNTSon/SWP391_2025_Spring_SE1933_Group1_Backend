package com.se1933g01.steam_clone_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.community.Message;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId =:converstaionId")
    List<Message> findAllByConversationId(@Param("conversationId") long conversationId);
}

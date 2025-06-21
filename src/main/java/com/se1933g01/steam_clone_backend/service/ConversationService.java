package com.se1933g01.steam_clone_backend.service;

import org.springframework.stereotype.Service;

import com.se1933g01.steam_clone_backend.repository.ConversationRepo;
import com.se1933g01.steam_clone_backend.repository.MessageRepo;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Service
public class ConversationService {

    private final ConversationRepo conversationRepo;
    private final MessageRepo messageRepo;

    public ConversationService(ConversationRepo conversationRepo, MessageRepo messageRepo) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
    }

}

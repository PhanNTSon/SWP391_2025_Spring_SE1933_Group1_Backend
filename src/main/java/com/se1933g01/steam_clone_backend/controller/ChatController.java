package com.se1933g01.steam_clone_backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.se1933g01.steam_clone_backend.dto.ChatMessageDTO;

/**
 * @author Phan NT Son
 * @since 22-06-2025
 */
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendPrivateMessage(Principal principal, ChatMessageDTO msg) {
        msg.setSenderUsername(principal.getName());
        System.out.println("principal" + principal);
        msg.setSentAt(LocalDateTime.now());
        messagingTemplate.convertAndSendToUser(msg.getReceiverUsername(), "/queue/messages", msg);
    }
}

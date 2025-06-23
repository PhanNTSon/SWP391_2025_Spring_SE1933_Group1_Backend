package com.se1933g01.steam_clone_backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.se1933g01.steam_clone_backend.dto.ChatMessageDTO;
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.service.CommunityService;

/**
 * @author Phan NT Son
 * @since 22-06-2025
 */
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CommunityService communityService;

    public ChatController(SimpMessagingTemplate messagingTemplate, CommunityService communityService) {
        this.messagingTemplate = messagingTemplate;
        this.communityService = communityService;
    }

    @MessageMapping("/chat.send")
    public void sendPrivateMessage(Principal principal, ChatMessageDTO msg,
            @AuthenticationPrincipal CustomUserDetail me) {
        msg.setSenderUsername(principal.getName());
        msg.setSentAt(LocalDateTime.now());
        communityService.saveMessage(msg, me.getUser().getUserId());

        messagingTemplate.convertAndSendToUser(msg.getReceiverUsername(), "/queue/messages", msg);
    }
}

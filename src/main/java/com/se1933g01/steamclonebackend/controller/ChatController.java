package com.se1933g01.steamclonebackend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.se1933g01.steamclonebackend.dto.ChatMessageDTO;
import com.se1933g01.steamclonebackend.realtime.OnlineUserTracker;
import com.se1933g01.steamclonebackend.service.CommunityService;

/**
 * @author Phan NT Son
 * @since 22-06-2025
 */
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CommunityService communityService;
    private final OnlineUserTracker tracker;

    public ChatController(SimpMessagingTemplate messagingTemplate, CommunityService communityService,
            OnlineUserTracker onlineUserTracker) {
        this.messagingTemplate = messagingTemplate;
        this.communityService = communityService;
        this.tracker = onlineUserTracker;
    }

    @MessageMapping("/chat.send")
    public void sendPrivateMessage(Principal principal, ChatMessageDTO msg) {
        msg.setSenderUsername(principal.getName());
        msg.setSentAt(LocalDateTime.now());
        communityService.saveMessage(msg, msg.getSenderUsername());

        messagingTemplate.convertAndSendToUser(msg.getReceiverUsername(), "/queue/messages/" + msg.getSenderUsername(),
                msg);
    }

    @SubscribeMapping("/online")
    public Set<String> initialList() {
        return tracker.getAllOnlineUsers();
    }
}

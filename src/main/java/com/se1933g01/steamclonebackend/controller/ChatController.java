package com.se1933g01.steamclonebackend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.se1933g01.steamclonebackend.dto.community.GroupChatMessageDTO;
import com.se1933g01.steamclonebackend.dto.community.MessageDTO;
import com.se1933g01.steamclonebackend.dto.community.PrivateChatMessageDTO;
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

    @MessageMapping("/chat/private.send")
    public void sendPrivateMessage(Principal principal, PrivateChatMessageDTO msg) {
        

        msg.setSenderUsername(principal.getName());
        msg.setSentAt(LocalDateTime.now());
        communityService.saveMessage(msg, msg.getSenderUsername());

        MessageDTO sendBack = new MessageDTO(
                msg.getSenderId(),
                msg.getSenderUsername(),
                msg.getContent(),
                msg.getSentAt());

        messagingTemplate.convertAndSendToUser(msg.getReceiverUsername(), "/queue/messages/" + msg.getSenderUsername(),
                sendBack);
        messagingTemplate.convertAndSendToUser(msg.getSenderUsername(), "/queue/messages/" + msg.getReceiverUsername(),
                sendBack);
    }

    @MessageMapping("/chat/group.send")
    public void sendGroupMessage(Principal principal, GroupChatMessageDTO msg) {
        msg.setSentAt(LocalDateTime.now());
        communityService.saveGroupMessage(msg);

        MessageDTO sendBack = new MessageDTO();
        sendBack.setSenderId(msg.getSenderId());
        sendBack.setSenderName(principal.getName());
        sendBack.setMessageContent(msg.getContent());
        sendBack.setSentAt(msg.getSentAt());

        messagingTemplate.convertAndSend("/topic/group/" + msg.getGroupId() + "/messages", sendBack);
    }

    @SubscribeMapping("/online")
    public Set<String> initialList() {
        return tracker.getAllOnlineUsers();
    }
}

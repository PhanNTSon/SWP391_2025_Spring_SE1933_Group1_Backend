package com.se1933g01.steamclonebackend.realtime;

import java.security.Principal;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

    private final OnlineUserTracker tracker;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(OnlineUserTracker tracker,
                                  SimpMessagingTemplate messagingTemplate) {
        this.tracker = tracker;
        this.messagingTemplate = messagingTemplate;
    }

    // Khi một session mới CONNECT
    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = h.getUser();
        String sessionId = h.getSessionId();

        if (user != null && sessionId != null) {
            tracker.add(sessionId, user.getName());
        }
    }

    // Khi một session DISCONNECT
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = h.getSessionId();

        if (sessionId != null) {
            tracker.remove(sessionId);
        }
    }

    // Khi client SUBSCRIBE vào /user/queue/online
    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        String dest = h.getDestination();     // ví dụ "/user/queue/online"
        Principal user = h.getUser();

        if ("/user/queue/online".equals(dest) && user != null) {
            Set<String> list = tracker.getAllOnlineUsers();
            // gửi initial list ngay khi subscribe thành công
            messagingTemplate.convertAndSendToUser(
                user.getName(),
                "/queue/online",
                list
            );
        }
    }

    // Mỗi khi có thêm hoặc bớt user, broadcast tới từng user qua user-queue
    private void broadcastAll() {
        Set<String> list = tracker.getAllOnlineUsers();
        for (String username : list) {
            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/online",
                list
            );
        }
    }

    // Gọi broadcastAll() sau mỗi connect/disconnect
    @EventListener
    public void afterConnect(SessionConnectEvent event) { broadcastAll(); }

    @EventListener
    public void afterDisconnect(SessionDisconnectEvent event) { broadcastAll(); }
}

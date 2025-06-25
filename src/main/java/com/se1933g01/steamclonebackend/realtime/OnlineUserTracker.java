package com.se1933g01.steamclonebackend.realtime;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

/**
 * @author Phan NT Son
 * @since 25/06/2025
 * 
 */
@Component
public class OnlineUserTracker {
    // Map sessionId -> username
    private final ConcurrentMap<String, String> sessionToUser = new ConcurrentHashMap<>();

    // Map username -> set of sessionIds
    private final ConcurrentMap<String, Set<String>> userSessions = new ConcurrentHashMap<>();

    public void add(String sessionId, String username) {
        sessionToUser.put(sessionId, username);
        userSessions.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void remove(String sessionId) {
        String username = sessionToUser.remove(sessionId);
        if (username == null) return;

        Set<String> sessions = userSessions.get(username);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessions.remove(username);
            }
        }
    }

    public Set<String> getAllOnlineUsers() {
        return Collections.unmodifiableSet(userSessions.keySet());
    }
}

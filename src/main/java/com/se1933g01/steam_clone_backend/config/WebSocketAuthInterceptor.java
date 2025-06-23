package com.se1933g01.steam_clone_backend.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.se1933g01.steam_clone_backend.service.UserDetailsServiceImpl;
import com.se1933g01.steam_clone_backend.utils.JwtUtil;

public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSocketAuthInterceptor(JwtUtil jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("token");

            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());

                accessor.setUser(auth);
            }
        }
        return message;
    }
}

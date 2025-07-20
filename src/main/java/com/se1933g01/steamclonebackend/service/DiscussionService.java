package com.se1933g01.steamclonebackend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.community.CreateThreadRequestDTO;
import com.se1933g01.steamclonebackend.entity.community.DiscussionThread;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.DiscussionThreadRepo;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionThreadRepo threadRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GameRepo gameRepo;

    public DiscussionThread createThread(Long userId, CreateThreadRequestDTO dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Game game = null;
        if (dto.getGameId() != null) {
            game = gameRepo.findById(dto.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found"));
        }

        DiscussionThread thread = new DiscussionThread();
        thread.setTitle(dto.getTitle());
        thread.setContent(dto.getContent());
        thread.setCreatedAt(LocalDateTime.now());
        thread.setUser(user);
        thread.setGame(game);

        return threadRepo.save(thread);
    }
}

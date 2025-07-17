package com.se1933g01.steamclonebackend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.community.CreateCommentRequestDTO;
import com.se1933g01.steamclonebackend.dto.community.CreateThreadRequestDTO;
import com.se1933g01.steamclonebackend.dto.community.DiscussionCommentDTO;
import com.se1933g01.steamclonebackend.dto.community.DiscussionThreadDTO;
import com.se1933g01.steamclonebackend.entity.community.DiscussionComment;
import com.se1933g01.steamclonebackend.entity.community.DiscussionThread;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.DiscussionCommentRepo;
import com.se1933g01.steamclonebackend.repository.DiscussionThreadRepo;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityPageService {

    private final DiscussionThreadRepo threadRepo;
    private final DiscussionCommentRepo commentRepo;
    private final UserRepo userRepo;
    private final GameRepo gameRepo;

    public List<DiscussionThreadDTO> getAllThreads() {
        return threadRepo.findAll().stream().map(thread ->
            new DiscussionThreadDTO(
                thread.getThreadId(),
                thread.getTitle(),
                thread.getContent(),
                thread.getCreatedAt(),
                thread.getUser().getUsername()
            )
        ).toList();
    }

    public DiscussionThreadDTO getThread(Long id) {
        DiscussionThread thread = threadRepo.findById(id).orElseThrow();
        return new DiscussionThreadDTO(
            thread.getThreadId(),
            thread.getTitle(),
            thread.getContent(),
            thread.getCreatedAt(),
            thread.getUser().getUsername()
        );
    }

    public List<DiscussionCommentDTO> getComments(Long threadId) {
        return commentRepo.findByThread_ThreadId(threadId).stream().map(comment ->
            new DiscussionCommentDTO(
                comment.getCommentId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getUsername()
            )
        ).toList();
    }

    public DiscussionThreadDTO createThread(CreateThreadRequestDTO request, String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Game game = request.getGameId() != null ? gameRepo.findById(request.getGameId()).orElse(null) : null;

        DiscussionThread thread = new DiscussionThread();
        thread.setTitle(request.getTitle());
        thread.setContent(request.getContent());
        thread.setUser(user);
        thread.setGame(game);
        thread.setCreatedAt(LocalDateTime.now());

        thread = threadRepo.save(thread);

        return new DiscussionThreadDTO(
            thread.getThreadId(),
            thread.getTitle(),
            thread.getContent(),
            thread.getCreatedAt(),
            user.getUsername()
        );
    }

    public DiscussionCommentDTO createComment(Long threadId, CreateCommentRequestDTO request, String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        DiscussionThread thread = threadRepo.findById(threadId).orElseThrow();

        DiscussionComment comment = new DiscussionComment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setThread(thread);

        comment = commentRepo.save(comment);

        return new DiscussionCommentDTO(
            comment.getCommentId(),
            comment.getContent(),
            comment.getCreatedAt(),
            user.getUsername()
        );
    }
}

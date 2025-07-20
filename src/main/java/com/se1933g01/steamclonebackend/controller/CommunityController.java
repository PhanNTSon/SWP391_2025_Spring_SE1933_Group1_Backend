package com.se1933g01.steamclonebackend.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.community.CreateCommentRequestDTO;
import com.se1933g01.steamclonebackend.dto.community.CreateThreadRequestDTO;
import com.se1933g01.steamclonebackend.dto.community.DiscussionCommentDTO;
import com.se1933g01.steamclonebackend.dto.community.DiscussionThreadDTO;
import com.se1933g01.steamclonebackend.dto.community.UpdateThreadRequestDTO;
import com.se1933g01.steamclonebackend.service.CommunityPageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPageService service;

    @GetMapping
    public List<DiscussionThreadDTO> getAllThreads() {
        return service.getAllThreads();
    }

    @GetMapping("/{id}")
    public DiscussionThreadDTO getThread(@PathVariable Long id) {
        return service.getThread(id);
    }

    @GetMapping("/{id}/comments")
    public List<DiscussionCommentDTO> getComments(@PathVariable Long id) {
        return service.getComments(id);
    }

    @PostMapping
    public DiscussionThreadDTO createThread(
            @RequestBody CreateThreadRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return service.createThread(request, userDetails.getUsername());
    }

    @PostMapping("/{id}/comments")
    public DiscussionCommentDTO createComment(
            @PathVariable Long id,
            @RequestBody CreateCommentRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return service.createComment(id, request, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public DiscussionThreadDTO updateThread(
            @PathVariable Long id,
            @RequestBody UpdateThreadRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return service.updateThread(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public void deleteThread(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        service.deleteThread(id, userDetails.getUsername());
    }
}

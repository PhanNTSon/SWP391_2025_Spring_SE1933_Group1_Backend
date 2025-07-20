package com.se1933g01.steamclonebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.se1933g01.steamclonebackend.dto.community.CreateThreadRequestDTO;
import com.se1933g01.steamclonebackend.entity.community.DiscussionThread;
import com.se1933g01.steamclonebackend.service.DiscussionService;

@RestController
@RequestMapping("/api/community")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping("/thread/create/{userId}")
    public ResponseEntity<DiscussionThread> createThread(
            @PathVariable Long userId,
            @RequestBody CreateThreadRequestDTO dto
    ) {
        DiscussionThread thread = discussionService.createThread(userId, dto);
        return ResponseEntity.ok(thread);
    }

    
}

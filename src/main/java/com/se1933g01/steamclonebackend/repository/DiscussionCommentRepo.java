package com.se1933g01.steamclonebackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steamclonebackend.entity.community.DiscussionComment;

public interface DiscussionCommentRepo extends JpaRepository<DiscussionComment, Long> {
    List<DiscussionComment> findByThread_ThreadId(Long threadId);
}

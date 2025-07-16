package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steamclonebackend.entity.community.GroupChat;

public interface GroupChatRepo extends JpaRepository<GroupChat, Long> {

}

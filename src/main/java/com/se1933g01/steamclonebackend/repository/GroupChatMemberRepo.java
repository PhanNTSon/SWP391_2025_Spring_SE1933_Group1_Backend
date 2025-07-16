package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steamclonebackend.entity.community.GroupChatMember;
import com.se1933g01.steamclonebackend.entity.community.GroupChatMemberId;

public interface GroupChatMemberRepo extends JpaRepository<GroupChatMember, GroupChatMemberId> {

}

package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.GroupChatMember;
import com.se1933g01.steamclonebackend.entity.community.GroupChatMemberId;

@Repository
public interface GroupChatMemberRepo extends JpaRepository<GroupChatMember, GroupChatMemberId> {

    @Query("SELECT gcm FROM GroupChatMember gcm WHERE gcm.member.userId = :memberId")
    Optional<List<GroupChatMember>> findAllGroupsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT gcm FROM GroupChatMember gcm WHERE gcm.group.groupId = :groupId")
    Optional<List<GroupChatMember>> findAllMembersByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT gcm FROM GroupChatMember gcm WHERE gcm.member.userId = :memberId AND gcm.group.groupId = :groupId")
    Optional<GroupChatMember> findByMemberIdAndGroupId(@Param("memberId") Long memberId,
            @Param("groupId") Long groupId);
}

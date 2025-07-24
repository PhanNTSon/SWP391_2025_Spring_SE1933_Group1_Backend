package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.GroupMessage;

@Repository
public interface GroupMessageRepo extends JpaRepository<GroupMessage, Long> {

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group.groupId = :groupChatId ")
    Optional<List<GroupMessage>> findAllByGroupChatId(@Param("groupChatId") Long groupChatId);
}

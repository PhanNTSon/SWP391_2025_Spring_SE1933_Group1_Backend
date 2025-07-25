package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.Block;
import com.se1933g01.steamclonebackend.entity.community.BlockId;

@Repository
public interface BlockRepo extends JpaRepository<Block, BlockId> {

    @Query("SELECT b FROM Block b WHERE b.blocker.userId = :blockerId")
    Optional<List<Block>> findAllByBlockerId(@Param("blockerId") Long blockerId);

    @Query("SELECT b FROM Block b WHERE b.blocked.userId = :blockedId")
    Optional<List<Block>> findAllByBlockedId(@Param("blockedId") Long blockedId);

    @Query("""
                SELECT b
                FROM Block b
                WHERE b.blocker.userId = :blockerId AND b.blocked.userId = :blockedId
                OR b.blocker.userId = :blockedId AND b.blocked.userId = :blockerId
            """)
    Optional<Block> findByBlockerIdAndBlockedId(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);

}

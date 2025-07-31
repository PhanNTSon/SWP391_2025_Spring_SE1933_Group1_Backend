package com.se1933g01.steamclonebackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.FamilyInvitation;

@Repository
public interface FamilyInvitationRepo extends JpaRepository<FamilyInvitation, Long> {

    @Query("SELECT fi FROM FamilyInvitation fi WHERE fi.family.familyId = :familyId AND fi.receiver.userId = :friendId")
    Optional<FamilyInvitation> findByFamilyAndFriend(@Param("familyId") Long familyId,@Param("friendId") Long friendId);
}

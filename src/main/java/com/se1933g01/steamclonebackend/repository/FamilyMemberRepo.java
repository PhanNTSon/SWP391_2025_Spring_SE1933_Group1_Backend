package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.FamilyMember;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyMemberId;

@Repository
public interface FamilyMemberRepo extends JpaRepository<FamilyMember, FamilyMemberId>{
 
    @Query("SELECT fm FROM FamilyMember fm WHERE fm.family.familyId = : familyId")
    List<FamilyMember> findAllMemberByFamily(@Param("familyId") Long familyId);

    @Query("SELECT fm FROM FamilyMember fm WHERE fm.user.userId = :userId")
    Optional<FamilyMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT fm FROM FamilyMember fm WHERE fm.family.familyId = :familyId AND fm.user.userId = :userId")
    Optional<FamilyMember> findByFamilyAndUser(@Param("familyId")Long familyId, @Param("userId") Long userId);
}

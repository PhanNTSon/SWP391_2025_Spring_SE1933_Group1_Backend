package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.FamilyInvitation;

@Repository
public interface FamilyInvitationRepo extends JpaRepository<FamilyInvitation, Long> {

}

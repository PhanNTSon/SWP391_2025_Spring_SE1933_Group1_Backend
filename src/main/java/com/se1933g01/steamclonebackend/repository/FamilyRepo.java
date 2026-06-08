package com.se1933g01.steamclonebackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.Family;

@Repository
public interface FamilyRepo extends JpaRepository<Family, Long> {

    @Query("SELECT f FROM Family f WHERE f.owner.userId = :ownerId")
    Optional<Family> findByOwner(@Param("ownerId") Long ownerId);

}

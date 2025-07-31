package com.se1933g01.steamclonebackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.SubscriptionPlan;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan, Long> {

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.familyId = :familyId ORDER BY sp.startAt DESC")
    Optional<SubscriptionPlan> findLastestByFamilyId(@Param("familyId") Long familyId);
}
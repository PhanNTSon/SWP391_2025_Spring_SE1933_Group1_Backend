package com.se1933g01.steamclonebackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.SubscriptionPlan;

@Repository
public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findTopByFamilyIdOrderByStartAtDesc(Long familyId);
    Optional<SubscriptionPlan> findTopByFamilyIdOrderByEndAtDesc(Long familyId);
}
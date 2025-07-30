package com.se1933g01.steamclonebackend.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.family.SubscriptionPlanDTO;
import com.se1933g01.steamclonebackend.entity.community.family.Family;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyMember;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyMemberId;
import com.se1933g01.steamclonebackend.entity.community.family.SubscriptionPlan;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.FamilyRepo;
import com.se1933g01.steamclonebackend.repository.SubscriptionPlanRepo;

import jakarta.persistence.EntityManager;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepo sPlanRepo;
    private final EntityManager entityManager;
    private final FamilyRepo familyRepo;

    public SubscriptionPlanService(SubscriptionPlanRepo sPlanRepo, EntityManager entityManager, FamilyRepo familyRepo) {
        this.sPlanRepo = sPlanRepo;
        this.entityManager = entityManager;
        this.familyRepo = familyRepo;
    }

    private BigDecimal calculatePrice(Long durationDays) {
        if (durationDays <= 30) {
            return new BigDecimal("9.99"); // Monthly price
        } else if (durationDays <= 365) {
            return new BigDecimal("99.99"); // Yearly price
        } else {
            throw new IllegalArgumentException("Invalid duration for subscription plan");
        }
    }

    public SubscriptionPlanDTO subscribePlan(Long userId, Long duration) {
        User owner = entityManager.getReference(User.class, userId);

        // 1. Check if User have Family
        Family family = familyRepo.findByOwner(userId).orElse(null);
        // If not then create new
        if (family == null) {
            family = new Family();
            family.setCreatedAt(LocalDate.now());
            family.setOwner(owner);

            entityManager.persist(family);

            FamilyMemberId memberId = new FamilyMemberId(family.getFamilyId(), userId);
            FamilyMember member = new FamilyMember(memberId, family, owner, true, LocalDate.now());

            entityManager.persist(member);
        }

        // 3. Xác định giá gói
        BigDecimal price = calculatePrice(duration);

        // 4. Kiểm tra ví
        if (owner.getWalletBalance().compareTo(price) < 0) {
            throw new IllegalStateException("Insufficient balance in wallet");
        }

        // 5. Trừ tiền
        owner.setWalletBalance(owner.getWalletBalance().subtract(price));
        entityManager.persist(owner);

        // 6. Tính thời gian gia hạn
        LocalDate now = LocalDate.now();
        LocalDate currentExp = family.getExpDate();
        LocalDate startAt = (currentExp != null && currentExp.isAfter(now)) ? currentExp : now;
        LocalDate endAt = startAt.plusDays(duration);

        // 7. Tạo SubscriptionPlan record
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setFamilyId(family.getFamilyId());
        plan.setPlanName("Family Subscription Plan");
        plan.setDurationInDays(duration.intValue());
        plan.setStartAt(startAt);
        plan.setEndAt(endAt);
        plan.setPrice(price);
        plan.setCreatedAt(LocalDate.now());

        entityManager.persist(plan);

        // 8. Cập nhật ExpiredAt cho Family
        family.setExpDate(endAt);
        entityManager.persist(family);

        // 9. Trả về DTO cho FE
        return SubscriptionPlanDTO.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .startAt(startAt)
                .endAt(endAt)
                .price(price)
                .build();

    }

}

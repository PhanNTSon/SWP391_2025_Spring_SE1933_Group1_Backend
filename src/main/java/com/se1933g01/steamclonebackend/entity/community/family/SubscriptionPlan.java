package com.se1933g01.steamclonebackend.entity.community.family;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "SubscriptionPlan", schema = "public")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanID")
    private Long planId;

    @Column(name = "FamilyID", nullable = false)
    private Long familyId;

    @Column(name = "PlanName", nullable = false, length = 50)
    private String planName;

    @Column(name = "DurationInDays", nullable = false)
    private Integer durationInDays;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "StartAt", nullable = false)
    private LocalDate startAt;

    @Column(name = "EndAt", nullable = false)
    private LocalDate endAt;

    @Column(name = "Note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDate createdAt;
}

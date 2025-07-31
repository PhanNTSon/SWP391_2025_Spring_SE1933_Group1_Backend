package com.se1933g01.steamclonebackend.dto.family;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanDTO {
    private Long planId;
    private String planName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal price;
    private Integer duration;
}

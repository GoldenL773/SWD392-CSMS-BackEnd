package com.csms.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    private String name;
    private String description;
    private String discountType; // PERCENTAGE or FIXED
    private BigDecimal discountValue;
    private String applyTo; // PRODUCT or COMBO
    private Long targetId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}

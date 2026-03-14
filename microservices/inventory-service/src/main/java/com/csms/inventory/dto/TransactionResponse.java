package com.csms.inventory.dto;

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
public class TransactionResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String transactionType;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private LocalDateTime transactionDate;
    private String note;
    private LocalDateTime createdAt;
}

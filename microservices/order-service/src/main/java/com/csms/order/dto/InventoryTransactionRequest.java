package com.csms.order.dto;

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
public class InventoryTransactionRequest {
    private Long ingredientId;
    private String transactionType;
    private BigDecimal quantity;
    private LocalDateTime transactionDate;
    private String note;
}

package com.csms.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long ingredientId;
    private String transactionType;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private LocalDateTime transactionDate;
}

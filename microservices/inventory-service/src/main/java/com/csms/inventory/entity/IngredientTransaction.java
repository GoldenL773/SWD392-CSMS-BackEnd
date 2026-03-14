package com.csms.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredient_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false, length = 20)
    private String transactionType; // e.g., IMPORT, EXPORT, WASTE, ADJUSTMENT

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice; // the price at the time of transaction

    @Column(nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;
}

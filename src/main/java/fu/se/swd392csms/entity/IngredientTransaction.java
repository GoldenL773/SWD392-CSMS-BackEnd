package fu.se.swd392csms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IngredientTransaction Entity
 * Logs all inventory movements (import/export)
 */
@Entity
@Table(name = "ingredient_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee; // Employee performing the transaction
    
    @Column(nullable = false)
    private String type; // Import, Export
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity; // Amount of ingredient moved
    
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal pricePerUnit; // Price at transaction time

    private String supplier; // For import transactions

    private String note;

    private String notes; // Alias for note (plural form)
}

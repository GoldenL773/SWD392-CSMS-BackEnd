package com.csms.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientRequest {
    @NotBlank(message = "Ingredient name is required")
    private String name;
    
    @NotBlank(message = "Unit is required")
    private String unit;
    
    @NotNull(message = "Current stock is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Current stock must be zero or greater")
    private BigDecimal currentStock;
    
    @NotNull(message = "Min stock is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Min stock must be zero or greater")
    private BigDecimal minStock;
    
    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit cost must be zero or greater")
    private BigDecimal unitCost;
}

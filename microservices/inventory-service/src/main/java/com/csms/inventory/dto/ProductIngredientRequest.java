package com.csms.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductIngredientRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    private BigDecimal quantity;
}

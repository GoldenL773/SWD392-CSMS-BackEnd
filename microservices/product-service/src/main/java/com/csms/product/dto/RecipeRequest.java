package com.csms.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RecipeRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private String instructions;

    private Integer prepTime;

    @NotNull(message = "Ingredients are required")
    private List<IngredientRequest> ingredients;

    @Data
    public static class IngredientRequest {
        @NotNull(message = "Ingredient ID is required")
        private Long ingredientId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private BigDecimal quantity;

        @NotNull(message = "Unit is required")
        private String unit;
    }
}

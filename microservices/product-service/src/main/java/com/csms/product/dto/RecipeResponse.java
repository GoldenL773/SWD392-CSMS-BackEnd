package com.csms.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String instructions;
    private Integer prepTime;
    private List<IngredientResponse> ingredients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientResponse {
        private Long ingredientId;
        private String ingredientName;
        private BigDecimal quantity;
        private String unit;
    }
}

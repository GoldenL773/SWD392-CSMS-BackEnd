package com.csms.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredientResponse {
    private Long id;
    private Long productId;
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantity;
}

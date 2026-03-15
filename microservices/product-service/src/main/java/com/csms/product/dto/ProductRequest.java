package com.csms.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;
    
    // Accept categoryId OR category name string from frontend
    private Long categoryId;
    
    // frontend sends category as string name, not ID
    private String category;
    
    private Boolean available = true;
    
    // Status from frontend (maps to available flag)
    private String status;

    private String imageUrl;

    private java.util.List<VariantRequest> variants;
    private java.util.List<IngredientRequest> ingredients;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VariantRequest {
        private Long id;
        private String size;
        private String temperature;
        private BigDecimal price;
        private String sku;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IngredientRequest {
        private Long ingredientId;
        private BigDecimal quantity;
        private String unit;
    }
}


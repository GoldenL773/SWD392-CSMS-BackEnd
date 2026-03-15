package com.csms.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemRequest {
    // productId is nullable for combo items
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Long variantId;
    private Long comboId;
    private String type; // PRODUCT, COMBO
    private java.math.BigDecimal price;
    
    // Custom validation: must have either productId or comboId
    @AssertTrue(message = "Either productId or comboId must be provided")
    public boolean isValidItem() {
        return productId != null || comboId != null;
    }
}

package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Response DTO
 * Used for returning product information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private String status;
    private String description;
    private List<ProductIngredientInfo> productIngredients;
    
    // Availability status fields
    private String availabilityStatus; // "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK"
    private Boolean isAvailable; // true if product can be ordered
    private Boolean isLowStock; // true if any ingredient is below minimum stock
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductIngredientInfo {
        private Long ingredientId;
        private String ingredientName;
        private BigDecimal requiredQuantity;
        private String unit;
        private BigDecimal currentQuantity; // Current ingredient stock
        private BigDecimal minimumStock; // Minimum stock threshold
        private Boolean isLowStock; // true if current < minimum
    }
}

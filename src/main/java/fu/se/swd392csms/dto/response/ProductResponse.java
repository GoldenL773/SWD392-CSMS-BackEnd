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
    private List<ProductIngredientInfo> ingredients;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductIngredientInfo {
        private Long ingredientId;
        private String ingredientName;
        private BigDecimal quantityRequired;
        private String unit;
    }
}

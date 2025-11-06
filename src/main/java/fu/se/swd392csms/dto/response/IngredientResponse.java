package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ingredient Response DTO
 * Used for returning ingredient information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {
    
    private Long id;
    private String name;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal minimumStock;
    private Boolean isLowStock;
}

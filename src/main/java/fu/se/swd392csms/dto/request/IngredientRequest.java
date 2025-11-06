package fu.se.swd392csms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ingredient Request DTO
 * Used for creating and updating ingredients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequest {
    
    @NotBlank(message = "Ingredient name is required")
    private String name;
    
    @NotBlank(message = "Unit is required")
    private String unit;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", message = "Quantity must be non-negative")
    private BigDecimal quantity;
    
    @NotNull(message = "Minimum stock is required")
    @DecimalMin(value = "0.0", message = "Minimum stock must be non-negative")
    private BigDecimal minimumStock;
}

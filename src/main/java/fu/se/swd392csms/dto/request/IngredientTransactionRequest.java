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
 * Ingredient Transaction Request DTO
 * Used for recording ingredient transactions (import/export)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTransactionRequest {
    
    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;
    
    @NotBlank(message = "Transaction type is required")
    private String type; // IMPORT or EXPORT
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String notes;
}

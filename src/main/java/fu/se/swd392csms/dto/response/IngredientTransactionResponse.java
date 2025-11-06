package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ingredient Transaction Response DTO
 * Used for returning transaction information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientTransactionResponse {
    
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String type;
    private BigDecimal quantity;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime transactionDate;
    private String notes;
}

package fu.se.swd392csms.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Order Request DTO
 * Used for creating new orders
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;
}

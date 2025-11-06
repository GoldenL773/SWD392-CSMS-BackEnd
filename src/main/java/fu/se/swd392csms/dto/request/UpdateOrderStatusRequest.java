package fu.se.swd392csms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating order status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    /**
     * New status for the order
     * Valid values: PENDING, PROCESSING, COMPLETED, CANCELLED
     */
    @NotBlank(message = "Status is required")
    private String status;
}

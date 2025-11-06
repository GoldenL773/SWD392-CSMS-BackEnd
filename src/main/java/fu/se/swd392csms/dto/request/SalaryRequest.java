package fu.se.swd392csms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Salary Request DTO
 * Used for creating and updating salary records
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Month is required")
    private Integer month;
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Base salary is required")
    private BigDecimal baseSalary;
    
    private BigDecimal bonus;
    
    private BigDecimal deductions;
    
    @NotNull(message = "Total salary is required")
    private BigDecimal totalSalary;
    
    private String status;
    
    private String notes;
}

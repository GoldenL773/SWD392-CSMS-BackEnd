package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Salary Response DTO
 * Used for returning salary information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Integer month;
    private Integer year;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deductions;
    private BigDecimal totalSalary;
    private String status;
    private LocalDateTime paymentDate;
    private String notes;
}

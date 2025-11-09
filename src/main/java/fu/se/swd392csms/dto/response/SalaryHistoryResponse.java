package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Salary history item returned to clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryHistoryResponse {
    private Long id;
    private Long salaryId;
    private LocalDate changeDate;
    private String changedByName;
    private String note;
    private BigDecimal oldBonus;
    private BigDecimal newBonus;
    private BigDecimal oldDeduction;
    private BigDecimal newDeduction;
    private BigDecimal oldTotalSalary;
    private BigDecimal newTotalSalary;
}

package fu.se.swd392csms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee Response DTO
 * Used for returning employee information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    
    private Long id;
    private String fullName;
    private String position;
    private String phone;
    private String email;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String status;
    private Long userId;
    private String username;
}

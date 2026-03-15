package com.csms.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private BigDecimal amount;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal deductions;
    private LocalDate paymentDate;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String status;
    private LocalDateTime createdAt;
}

package com.csms.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalaryResponse {
    private Long id;
    private Long employeeId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String status;
}

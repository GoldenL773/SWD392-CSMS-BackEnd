package com.csms.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String position;
    private LocalDate hireDate;
    private String phone;
    private String address;
    private java.math.BigDecimal baseSalary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

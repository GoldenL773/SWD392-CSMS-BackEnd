package com.csms.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequest {
    private Long userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Hire date is required")
    private LocalDate hireDate;

    private String phone;
    
    private String address;
    
    private java.math.BigDecimal baseSalary;
}

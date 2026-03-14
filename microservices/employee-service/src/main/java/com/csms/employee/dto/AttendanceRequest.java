package com.csms.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceRequest {
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalDateTime checkIn;
    
    private LocalDateTime checkOut;

    @NotBlank(message = "Status is required (PRESENT, ABSENT, LATE, HALF_DAY)")
    private String status;
}

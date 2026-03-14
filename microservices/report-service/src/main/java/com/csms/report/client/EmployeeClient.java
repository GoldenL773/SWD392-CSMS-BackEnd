package com.csms.report.client;

import com.csms.report.dto.SalaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    
    @GetMapping("/api/salaries")
    List<SalaryResponse> getAllSalaries();
}

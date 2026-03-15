package com.csms.employee.controller;

import com.csms.employee.dto.EmployeeRequest;
import com.csms.employee.dto.EmployeeResponse;
import com.csms.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(List.of(employeeService.getEmployeeByUserId(userId)));
        }
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeResponse> getCurrentEmployee(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Name", required = false) String username) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(employeeService.getEmployeeByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<EmployeeResponse> getEmployeeByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(employeeService.getEmployeeByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return new ResponseEntity<>(employeeService.createEmployee(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{employeeId}/salary")
    public ResponseEntity<List<com.csms.employee.dto.SalaryResponse>> getEmployeeSalaries(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        // Forwarding call to SalaryService since it's in the same microservice
        // Alternatively, frontend should call /api/salaries/employee/{employeeId} directly,
        // but adding this mapped endpoint to support the UI's employeeApi.js
        List<com.csms.employee.dto.SalaryResponse> allSalaries = employeeService.getEmployeeSalaries(employeeId);
        
        if (month != null && year != null) {
            allSalaries = allSalaries.stream()
                .filter(s -> month.equals(s.getMonth()) && year.equals(s.getYear()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        return ResponseEntity.ok(allSalaries);
    }
}

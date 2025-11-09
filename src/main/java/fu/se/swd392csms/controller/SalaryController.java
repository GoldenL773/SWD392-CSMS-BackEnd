package fu.se.swd392csms.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.SalaryResponse;
import fu.se.swd392csms.dto.response.SalaryHistoryResponse;
import fu.se.swd392csms.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Salary Management
 * Handles payroll calculation and salary operations
 */
@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
@Tag(name = "Salary", description = "Salary management APIs")
public class SalaryController {
    
    private final SalaryService salaryService;
    
    /**
     * Calculate monthly salaries for all active employees
     */
    @PostMapping("/calculate")
    @Operation(summary = "Calculate monthly salaries", description = "Calculate salaries for all active employees based on attendance")
    public ResponseEntity<List<SalaryResponse>> calculateMonthlySalaries(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<SalaryResponse> salaries = salaryService.calculateMonthlySalaries(month, year);
        return ResponseEntity.status(HttpStatus.CREATED).body(salaries);
    }
    
    /**
     * Get salary by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get salary by ID", description = "Retrieve salary details by ID")
    public ResponseEntity<SalaryResponse> getSalaryById(@PathVariable Long id) {
        SalaryResponse response = salaryService.getSalaryById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all salaries for an employee
     */
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get employee salaries", description = "Get all salary records for an employee")
    public ResponseEntity<List<SalaryResponse>> getEmployeeSalaries(@PathVariable Long employeeId) {
        List<SalaryResponse> responses = salaryService.getEmployeeSalaries(employeeId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get salaries for a specific month and year
     */
    @GetMapping("/period")
    @Operation(summary = "Get salaries by period", description = "Get all salaries for a specific month and year")
    public ResponseEntity<List<SalaryResponse>> getSalariesByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<SalaryResponse> responses = salaryService.getSalariesByMonthAndYear(month, year);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get all pending salaries
     */
    @GetMapping("/pending")
    @Operation(summary = "Get pending salaries", description = "Get all salaries with pending status")
    public ResponseEntity<List<SalaryResponse>> getPendingSalaries() {
        List<SalaryResponse> responses = salaryService.getPendingSalaries();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Create or update salary record
     */
    @PostMapping
    @Operation(summary = "Create/Update salary", description = "Manually create or update a salary record")
    public ResponseEntity<SalaryResponse> createOrUpdateSalary(@Valid @RequestBody SalaryRequest request) {
        SalaryResponse response = salaryService.createOrUpdateSalary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Update salary adjustments (bonus and deductions)
     */
    @PatchMapping("/{id}/adjustments")
    @Operation(summary = "Update salary adjustments", description = "Update bonus and deductions for a salary record")
    public ResponseEntity<SalaryResponse> updateSalaryAdjustments(
            @PathVariable Long id,
            @RequestBody Map<String, Object> adjustments) {
        
        BigDecimal bonus = adjustments.containsKey("bonus") ? 
                new BigDecimal(adjustments.get("bonus").toString()) : null;
        BigDecimal deductions = adjustments.containsKey("deductions") ? 
                new BigDecimal(adjustments.get("deductions").toString()) : null;
        Long changedBy = adjustments.containsKey("changedBy") ? 
                Long.valueOf(adjustments.get("changedBy").toString()) : 1L;
        String note = adjustments.containsKey("note") ? adjustments.get("note").toString() : null;
        
        SalaryResponse response = salaryService.updateSalaryAdjustments(id, bonus, deductions, changedBy, note);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark salary as paid
     */
    @PatchMapping("/{id}/mark-paid")
    @Operation(summary = "Mark as paid", description = "Mark a salary record as paid")
    public ResponseEntity<SalaryResponse> markAsPaid(@PathVariable Long id) {
        SalaryResponse response = salaryService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark multiple salaries as paid
     */
    @PostMapping("/mark-paid-batch")
    @Operation(summary = "Mark multiple as paid", description = "Mark multiple salary records as paid")
    public ResponseEntity<List<SalaryResponse>> markMultipleAsPaid(@RequestBody List<Long> ids) {
        List<SalaryResponse> responses = salaryService.markMultipleAsPaid(ids);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Delete salary record
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete salary", description = "Delete a salary record")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get total salary paid for a period
     */
    @GetMapping("/total-paid")
    @Operation(summary = "Get total paid", description = "Get total salary paid for a specific month and year")
    public ResponseEntity<Double> getTotalSalaryPaid(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        Double total = salaryService.getTotalSalaryPaid(month, year);
        return ResponseEntity.ok(total);
    }
    
    /**
     * Get all paid salaries
     */
    @GetMapping("/paid")
    @Operation(summary = "Get paid salaries", description = "Get all salaries with paid status")
    public ResponseEntity<List<SalaryResponse>> getPaidSalaries() {
        List<SalaryResponse> responses = salaryService.getPaidSalaries();
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get paid salaries by period
     */
    @GetMapping("/paid/period")
    @Operation(summary = "Get paid salaries by period", description = "Get paid salaries for a specific month and year")
    public ResponseEntity<List<SalaryResponse>> getPaidSalariesByPeriod(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<SalaryResponse> responses = salaryService.getPaidSalariesByPeriod(month, year);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get salary update history by salaryId
     */
    @GetMapping("/{salaryId}/history")
    @PreAuthorize("hasAnyAuthority('ADMIN','FINANCE')")
    @Operation(summary = "Get salary update history", description = "Retrieve audit history for a salary record")
    public ResponseEntity<List<SalaryHistoryResponse>> getSalaryHistory(@PathVariable Long salaryId) {
        List<SalaryHistoryResponse> history = salaryService.getSalaryHistory(salaryId);
        return ResponseEntity.ok(history);
    }
}

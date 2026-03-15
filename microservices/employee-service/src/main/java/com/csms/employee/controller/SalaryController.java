package com.csms.employee.controller;

import com.csms.employee.dto.SalaryRequest;
import com.csms.employee.dto.SalaryResponse;
import com.csms.employee.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping
    public ResponseEntity<List<SalaryResponse>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SalaryResponse>> getSalariesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(salaryService.getSalariesByEmployee(employeeId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<SalaryResponse>> getPendingSalaries() {
        return ResponseEntity.ok(salaryService.getPendingSalaries());
    }

    @GetMapping("/paid")
    public ResponseEntity<List<SalaryResponse>> getPaidSalaries() {
        return ResponseEntity.ok(salaryService.getPaidSalaries());
    }

    @PostMapping
    public ResponseEntity<SalaryResponse> createSalary(@Valid @RequestBody SalaryRequest request) {
        return new ResponseEntity<>(salaryService.createSalary(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryResponse> updateSalary(@PathVariable Long id, @Valid @RequestBody SalaryRequest request) {
        return ResponseEntity.ok(salaryService.updateSalary(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/calculate")
    public ResponseEntity<List<SalaryResponse>> calculateSalaries(@RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(salaryService.calculateMonthlySalaries(month, year));
    }

    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<SalaryResponse> markPaid(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.markAsPaid(id));
    }

    @PostMapping("/mark-paid-batch")
    public ResponseEntity<Void> markBatchPaid(@RequestBody List<Long> ids) {
        salaryService.markBatchAsPaid(ids);
        return ResponseEntity.ok().build();
    }
}

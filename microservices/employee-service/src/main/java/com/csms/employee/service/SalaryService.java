package com.csms.employee.service;

import com.csms.employee.dto.SalaryRequest;
import com.csms.employee.dto.SalaryResponse;
import com.csms.employee.entity.Employee;
import com.csms.employee.entity.Salary;
import com.csms.employee.exception.ResourceNotFoundException;
import com.csms.employee.repository.EmployeeRepository;
import com.csms.employee.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<SalaryResponse> getAllSalaries() {
        return salaryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> getSalariesByEmployee(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> getPendingSalaries() {
        return salaryRepository.findByStatus("PENDING").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> getPaidSalaries() {
        return salaryRepository.findByStatus("PAID").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SalaryResponse createSalary(SalaryRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        Salary salary = Salary.builder()
                .employee(employee)
                .amount(request.getAmount())
                .baseSalary(request.getBaseSalary())
                .bonus(request.getBonus())
                .deductions(request.getDeductions())
                .paymentDate(request.getPaymentDate())
                .periodStart(request.getPeriodStart())
                .periodEnd(request.getPeriodEnd())
                .status(request.getStatus().toUpperCase())
                .build();

        Salary savedSalary = salaryRepository.save(salary);
        return mapToResponse(savedSalary);
    }

    @Transactional
    public SalaryResponse updateSalary(Long id, SalaryRequest request) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with id: " + id));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        salary.setEmployee(employee);
        salary.setAmount(request.getAmount());
        salary.setBaseSalary(request.getBaseSalary());
        salary.setBonus(request.getBonus());
        salary.setDeductions(request.getDeductions());
        salary.setPaymentDate(request.getPaymentDate());
        salary.setPeriodStart(request.getPeriodStart());
        salary.setPeriodEnd(request.getPeriodEnd());
        salary.setStatus(request.getStatus().toUpperCase());

        Salary updatedSalary = salaryRepository.save(salary);
        return mapToResponse(updatedSalary);
    }

    @Transactional
    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salary record not found with id: " + id);
        }
        salaryRepository.deleteById(id);
    }

    @Transactional
    public List<SalaryResponse> calculateMonthlySalaries(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        
        List<Employee> employees = employeeRepository.findAll();
        List<Salary> generatedSalaries = new java.util.ArrayList<>();
        
        for (Employee emp : employees) {
            // Check if salary already exists for this period
            // (Simplification: just create/update)
            
            // Calculate based on attendance
            // For now, simple: amount = baseSalary (if exists) or default
            java.math.BigDecimal base = emp.getBaseSalary() != null ? emp.getBaseSalary() : new java.math.BigDecimal("5000000");
            
            Salary salary = Salary.builder()
                    .employee(emp)
                    .amount(base)
                    .baseSalary(base)
                    .bonus(java.math.BigDecimal.ZERO)
                    .deductions(java.math.BigDecimal.ZERO)
                    .periodStart(start)
                    .periodEnd(end)
                    .paymentDate(LocalDate.now().plusDays(5)) // Default payment date
                    .status("PENDING")
                    .build();
            
            generatedSalaries.add(salaryRepository.save(salary));
        }
        
        return generatedSalaries.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public SalaryResponse markAsPaid(Long id) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary not found: " + id));
        salary.setStatus("PAID");
        salary.setPaymentDate(LocalDate.now());
        return mapToResponse(salaryRepository.save(salary));
    }

    @Transactional
    public void markBatchAsPaid(List<Long> ids) {
        for (Long id : ids) {
            salaryRepository.findById(id).ifPresent(s -> {
                s.setStatus("PAID");
                s.setPaymentDate(LocalDate.now());
                salaryRepository.save(s);
            });
        }
    }

    private SalaryResponse mapToResponse(Salary salary) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployee().getId())
                .employeeName(salary.getEmployee().getFirstName() + " " + salary.getEmployee().getLastName())
                .amount(salary.getAmount())
                .baseSalary(salary.getBaseSalary() != null ? salary.getBaseSalary() : salary.getAmount())
                .bonus(salary.getBonus() != null ? salary.getBonus() : java.math.BigDecimal.ZERO)
                .deductions(salary.getDeductions() != null ? salary.getDeductions() : java.math.BigDecimal.ZERO)
                .paymentDate(salary.getPaymentDate())
                .periodStart(salary.getPeriodStart())
                .periodEnd(salary.getPeriodEnd())
                .month(salary.getPeriodStart() != null ? salary.getPeriodStart().getMonthValue() : null)
                .year(salary.getPeriodStart() != null ? salary.getPeriodStart().getYear() : null)
                .status(salary.getStatus())
                .createdAt(salary.getCreatedAt())
                .build();
    }
}

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

    private SalaryResponse mapToResponse(Salary salary) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployee().getId())
                .employeeName(salary.getEmployee().getFirstName() + " " + salary.getEmployee().getLastName())
                .amount(salary.getAmount())
                .paymentDate(salary.getPaymentDate())
                .periodStart(salary.getPeriodStart())
                .periodEnd(salary.getPeriodEnd())
                .status(salary.getStatus())
                .createdAt(salary.getCreatedAt())
                .build();
    }
}

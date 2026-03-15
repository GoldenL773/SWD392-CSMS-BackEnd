package com.csms.employee.service;

import com.csms.employee.dto.EmployeeRequest;
import com.csms.employee.dto.EmployeeResponse;
import com.csms.employee.entity.Employee;
import com.csms.employee.exception.ResourceNotFoundException;
import com.csms.employee.exception.ValidationException;
import com.csms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with user id: " + userId));
        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (request.getUserId() != null) {
            java.util.Optional<Employee> existing = employeeRepository.findFirstByUserId(request.getUserId());
            if (existing.isPresent()) {
                Employee employee = existing.get();
                if (request.getFirstName() != null && !request.getFirstName().isEmpty()) employee.setFirstName(request.getFirstName());
                if (request.getLastName() != null && !request.getLastName().isEmpty()) employee.setLastName(request.getLastName());
                if (request.getPosition() != null && !request.getPosition().isEmpty()) employee.setPosition(request.getPosition());
                if (request.getHireDate() != null) employee.setHireDate(request.getHireDate());
                if (request.getPhone() != null && !request.getPhone().isEmpty()) employee.setPhone(request.getPhone());
                if (request.getAddress() != null && !request.getAddress().isEmpty()) employee.setAddress(request.getAddress());
                if (request.getBaseSalary() != null) employee.setBaseSalary(request.getBaseSalary());
                return mapToResponse(employeeRepository.save(employee));
            }
        }

        Employee employee = Employee.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .position(request.getPosition())
                .hireDate(request.getHireDate())
                .phone(request.getPhone())
                .address(request.getAddress())
                .baseSalary(request.getBaseSalary())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (!employee.getUserId().equals(request.getUserId()) && employeeRepository.existsByUserId(request.getUserId())) {
            throw new ValidationException("Employee already exists for user ID: " + request.getUserId());
        }

        employee.setUserId(request.getUserId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPosition(request.getPosition());
        employee.setHireDate(request.getHireDate());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setBaseSalary(request.getBaseSalary());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .userId(employee.getUserId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .position(employee.getPosition())
                .hireDate(employee.getHireDate())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .baseSalary(employee.getBaseSalary())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}

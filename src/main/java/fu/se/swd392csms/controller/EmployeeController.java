package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.request.EmployeeRequest;
import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;
import fu.se.swd392csms.dto.response.EmployeeResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.SalaryResponse;
import fu.se.swd392csms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Employee management APIs")
public class EmployeeController {
    
    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employees", description = "Get all employees with optional status filter and pagination")
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(status, pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my employee profile", description = "Get the employee profile for the currently authenticated user")
    public ResponseEntity<EmployeeResponse> getMyEmployeeProfile() {
        EmployeeResponse employee = employeeService.getCurrentEmployeeProfile();
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee by ID", description = "Get a specific employee by their ID")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new employee", description = "Create a new employee with user account")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Update employee", description = "Update an existing employee")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete employee", description = "Delete an employee by ID")
    public ResponseEntity<MessageResponse> deleteEmployee(@PathVariable Long id) {
        MessageResponse response = employeeService.deleteEmployee(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/attendance")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee attendance", description = "Get attendance records for a specific employee")
    public ResponseEntity<Page<AttendanceResponse>> getEmployeeAttendance(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceResponse> attendance = employeeService.getEmployeeAttendance(id, startDate, endDate, pageable);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/attendance")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Add attendance record", description = "Record employee attendance")
    public ResponseEntity<AttendanceResponse> addAttendance(@Valid @RequestBody AttendanceRequest request) {
        AttendanceResponse attendance = employeeService.addAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    @GetMapping("/{id}/salary")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employee salary", description = "Get salary records for a specific employee")
    public ResponseEntity<Page<SalaryResponse>> getEmployeeSalary(
            @PathVariable Long id,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SalaryResponse> salary = employeeService.getEmployeeSalary(id, month, year, pageable);
        return ResponseEntity.ok(salary);
    }

    @PostMapping("/salary")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Add salary record", description = "Add salary record for an employee")
    public ResponseEntity<SalaryResponse> addSalary(@Valid @RequestBody SalaryRequest request) {
        SalaryResponse salary = employeeService.addSalary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(salary);
    }
}

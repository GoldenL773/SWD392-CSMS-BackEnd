package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.request.EmployeeRequest;
import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;
import fu.se.swd392csms.dto.response.EmployeeResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.SalaryResponse;
import fu.se.swd392csms.entity.*;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.*;
import fu.se.swd392csms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Employee Service Implementation
 * Handles business logic for employee management
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryRepository salaryRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Create a new employee with user account
     * @param request Employee creation request
     * @return Created employee response
     */
    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        // Validate username is provided
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        
        // Validate password is provided
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        
        // Validate username is unique
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        // Validate email is unique
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Create User account
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                roles.add(role);
            }
        } else {
            // Default role: STAFF
            Role staffRole = roleRepository.findByName("STAFF")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "STAFF"));
            roles.add(staffRole);
        }
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        
        // Create Employee
        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        
        Employee savedEmployee = employeeRepository.save(employee);
        
        return convertToEmployeeResponse(savedEmployee);
    }
    
    /**
     * Get employee by ID
     * @param id Employee ID
     * @return Employee response
     */
    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return convertToEmployeeResponse(employee);
    }
    
    /**
     * Get all employees with optional filtering and pagination
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of employees
     */
    @Override
    public Page<EmployeeResponse> getAllEmployees(String status, Pageable pageable) {
        Page<Employee> employeePage;

        if (status != null && !status.isEmpty()) {
            // Convert List to Page manually
            List<Employee> employees = employeeRepository.findByStatus(status);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), employees.size());
            List<Employee> pageContent = employees.subList(start, end);
            employeePage = new PageImpl<>(pageContent, pageable, employees.size());
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        List<EmployeeResponse> employeeResponses = employeePage.getContent().stream()
                .map(this::convertToEmployeeResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(employeeResponses, pageable, employeePage.getTotalElements());
    }
    
    /**
     * Update employee
     * @param id Employee ID
     * @param request Employee update request
     * @return Updated employee response
     */
    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Check email uniqueness if changed
        if (!employee.getEmail().equals(request.getEmail()) && 
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Update employee fields
        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setHireDate(request.getHireDate());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        
        Employee updatedEmployee = employeeRepository.save(employee);
        
        return convertToEmployeeResponse(updatedEmployee);
    }
    
    /**
     * Delete employee
     * @param id Employee ID
     * @return Success message
     */
    @Override
    @Transactional
    public MessageResponse deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        // Delete associated user account
        if (employee.getUser() != null) {
            userRepository.delete(employee.getUser());
        }
        
        // Delete employee
        employeeRepository.delete(employee);
        
        return new MessageResponse("Employee deleted successfully");
    }
    
    /**
     * Get employee attendance records
     * @param employeeId Employee ID
     * @param startDate Start date filter (optional)
     * @param endDate End date filter (optional)
     * @param pageable Pagination parameters
     * @return Page of attendance records
     */
    @Override
    public Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Verify employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        List<Attendance> attendanceList;

        if (startDate != null && endDate != null) {
            attendanceList = attendanceRepository.findByEmployeeIdAndCheckInTimeBetween(
                    employeeId, startDate, endDate);
        } else {
            attendanceList = attendanceRepository.findByEmployeeId(employeeId);
        }
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), attendanceList.size());
        
        List<AttendanceResponse> attendanceResponses = attendanceList.subList(start, end).stream()
                .map(attendance -> convertToAttendanceResponse(attendance, employee))
                .collect(Collectors.toList());
        
        return new PageImpl<>(attendanceResponses, pageable, attendanceList.size());
    }
    
    /**
     * Add attendance record
     * @param request Attendance request
     * @return Created attendance response
     */
    @Override
    @Transactional
    public AttendanceResponse addAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(request.getCheckInTime().toLocalDate());
        attendance.setCheckInTime(request.getCheckInTime().toLocalTime());
        attendance.setCheckOutTime(request.getCheckOutTime() != null ? request.getCheckOutTime().toLocalTime() : null);
        attendance.setStatus(request.getStatus() != null ? request.getStatus() : "Present");
        attendance.setNotes(request.getNotes());

        // Calculate total hours if check-out time is provided
        if (request.getCheckOutTime() != null) {
            Duration duration = Duration.between(request.getCheckInTime(), request.getCheckOutTime());
            BigDecimal hours = BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
            attendance.setTotalHours(hours);
        }
        
        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        return convertToAttendanceResponse(savedAttendance, employee);
    }
    
    /**
     * Get employee salary records
     * @param employeeId Employee ID
     * @param month Month filter (optional)
     * @param year Year filter (optional)
     * @param pageable Pagination parameters
     * @return Page of salary records
     */
    @Override
    public Page<SalaryResponse> getEmployeeSalary(Long employeeId, Integer month, Integer year, Pageable pageable) {
        // Verify employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        List<Salary> salaryList;

        if (month != null && year != null) {
            Optional<Salary> salaryOpt = salaryRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
            salaryList = salaryOpt.map(List::of).orElse(List.of());
        } else if (year != null) {
            salaryList = salaryRepository.findByEmployeeIdAndYear(employeeId, year);
        } else {
            salaryList = salaryRepository.findByEmployeeId(employeeId);
        }
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), salaryList.size());
        
        List<SalaryResponse> salaryResponses = salaryList.subList(start, end).stream()
                .map(salary -> convertToSalaryResponse(salary, employee))
                .collect(Collectors.toList());
        
        return new PageImpl<>(salaryResponses, pageable, salaryList.size());
    }
    
    /**
     * Add salary record
     * @param request Salary request
     * @return Created salary response
     */
    @Override
    @Transactional
    public SalaryResponse addSalary(SalaryRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        // Check if salary record already exists for this month/year
        Optional<Salary> existingSalary = salaryRepository.findByEmployeeIdAndMonthAndYear(
                request.getEmployeeId(), request.getMonth(), request.getYear());

        if (existingSalary.isPresent()) {
            throw new BadRequestException("Salary record already exists for this employee in " +
                    request.getMonth() + "/" + request.getYear());
        }
        
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonth(request.getMonth());
        salary.setYear(request.getYear());
        salary.setBaseSalary(request.getBaseSalary());
        salary.setBonus(request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO);
        salary.setDeductions(request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO);
        salary.setTotalSalary(request.getTotalSalary());
        salary.setStatus(request.getStatus() != null ? request.getStatus() : "Pending");
        salary.setPaymentDate(LocalDateTime.now());
        salary.setNotes(request.getNotes());
        
        Salary savedSalary = salaryRepository.save(salary);
        
        return convertToSalaryResponse(savedSalary, employee);
    }
    
    /**
     * Convert Employee entity to EmployeeResponse DTO
     */
    private EmployeeResponse convertToEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .dob(employee.getDob())
                .gender(employee.getGender())
                .position(employee.getPosition())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .hireDate(employee.getHireDate())
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .userId(employee.getUser() != null ? employee.getUser().getId() : null)
                .username(employee.getUser() != null ? employee.getUser().getUsername() : null)
                .build();
    }
    
    /**
     * Convert Attendance entity to AttendanceResponse DTO
     */
    private AttendanceResponse convertToAttendanceResponse(Attendance attendance, Employee employee) {
        LocalDateTime checkInDateTime = attendance.getDate() != null && attendance.getCheckInTime() != null
                ? attendance.getDate().atTime(attendance.getCheckInTime())
                : null;
        LocalDateTime checkOutDateTime = attendance.getDate() != null && attendance.getCheckOutTime() != null
                ? attendance.getDate().atTime(attendance.getCheckOutTime())
                : null;

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFullName())
                .checkInTime(checkInDateTime)
                .checkOutTime(checkOutDateTime)
                .totalHours(attendance.getTotalHours())
                .status(attendance.getStatus())
                .notes(attendance.getNotes())
                .build();
    }
    
    /**
     * Convert Salary entity to SalaryResponse DTO
     */
    private SalaryResponse convertToSalaryResponse(Salary salary, Employee employee) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFullName())
                .month(salary.getMonth())
                .year(salary.getYear())
                .baseSalary(salary.getBaseSalary())
                .bonus(salary.getBonus())
                .deductions(salary.getDeductions())
                .totalSalary(salary.getTotalSalary())
                .status(salary.getStatus())
                .paymentDate(salary.getPaymentDate())
                .notes(salary.getNotes())
                .build();
    }
}

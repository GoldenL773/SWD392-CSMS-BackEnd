package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.request.EmployeeRequest;
import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;
import fu.se.swd392csms.dto.response.EmployeeResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.SalaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Employee Service Interface
 * Handles business logic for employee management
 */
public interface EmployeeService {
    
    /**
     * Create a new employee with user account
     * @param request Employee creation request
     * @return Created employee response
     */
    EmployeeResponse createEmployee(EmployeeRequest request);
    
    /**
     * Get employee by ID
     * @param id Employee ID
     * @return Employee response
     */
    EmployeeResponse getEmployeeById(Long id);
    
    /**
     * Get all employees with optional filtering and pagination
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of employees
     */
    Page<EmployeeResponse> getAllEmployees(String status, Pageable pageable);
    
    /**
     * Update employee
     * @param id Employee ID
     * @param request Employee update request
     * @return Updated employee response
     */
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    
    /**
     * Delete employee
     * @param id Employee ID
     * @return Success message
     */
    MessageResponse deleteEmployee(Long id);
    
    /**
     * Get employee attendance records
     * @param employeeId Employee ID
     * @param startDate Start date filter (optional)
     * @param endDate End date filter (optional)
     * @param pageable Pagination parameters
     * @return Page of attendance records
     */
    Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Add attendance record
     * @param request Attendance request
     * @return Created attendance response
     */
    AttendanceResponse addAttendance(AttendanceRequest request);
    
    /**
     * Get employee salary records
     * @param employeeId Employee ID
     * @param month Month filter (optional)
     * @param year Year filter (optional)
     * @param pageable Pagination parameters
     * @return Page of salary records
     */
    Page<SalaryResponse> getEmployeeSalary(Long employeeId, Integer month, Integer year, Pageable pageable);
    
    /**
     * Add salary record
     * @param request Salary request
     * @return Created salary response
     */
    SalaryResponse addSalary(SalaryRequest request);
}

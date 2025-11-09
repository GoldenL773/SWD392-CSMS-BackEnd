package fu.se.swd392csms.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.request.EmployeeRequest;
import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;
import fu.se.swd392csms.dto.response.EmployeeResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.SalaryResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse getCurrentEmployeeProfile();

    Page<EmployeeResponse> getAllEmployees(String status, Pageable pageable);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    MessageResponse deleteEmployee(Long id);

    Page<AttendanceResponse> getEmployeeAttendance(Long employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    AttendanceResponse addAttendance(AttendanceRequest request);

    Page<SalaryResponse> getEmployeeSalary(Long employeeId, Integer month, Integer year, Pageable pageable);
    
    SalaryResponse addSalary(SalaryRequest request);
}

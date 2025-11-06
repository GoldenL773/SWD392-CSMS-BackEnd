package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Attendance operations
 */
public interface AttendanceService {
    
    /**
     * Employee check-in
     * @param employeeId Employee ID
     * @return Attendance response
     */
    AttendanceResponse checkIn(Long employeeId);
    
    /**
     * Employee check-out
     * @param employeeId Employee ID
     * @return Attendance response
     */
    AttendanceResponse checkOut(Long employeeId);
    
    /**
     * Get attendance by ID
     * @param id Attendance ID
     * @return Attendance response
     */
    AttendanceResponse getAttendanceById(Long id);
    
    /**
     * Get all attendance records for an employee
     * @param employeeId Employee ID
     * @return List of attendance records
     */
    List<AttendanceResponse> getEmployeeAttendance(Long employeeId);
    
    /**
     * Get attendance records for an employee within date range
     * @param employeeId Employee ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of attendance records
     */
    List<AttendanceResponse> getEmployeeAttendanceByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get all attendance records for a specific date
     * @param date Date
     * @return List of attendance records
     */
    List<AttendanceResponse> getAttendanceByDate(LocalDate date);
    
    /**
     * Get today's attendance for an employee
     * @param employeeId Employee ID
     * @return Attendance response or null if not found
     */
    AttendanceResponse getTodayAttendance(Long employeeId);
    
    /**
     * Create or update attendance record manually (for managers)
     * @param request Attendance request
     * @return Attendance response
     */
    AttendanceResponse createOrUpdateAttendance(AttendanceRequest request);
    
    /**
     * Delete attendance record
     * @param id Attendance ID
     */
    void deleteAttendance(Long id);
    
    /**
     * Get total working hours for an employee in a date range
     * @param employeeId Employee ID
     * @param startDate Start date
     * @param endDate End date
     * @return Total working hours
     */
    Double getTotalWorkingHours(Long employeeId, LocalDate startDate, LocalDate endDate);
}

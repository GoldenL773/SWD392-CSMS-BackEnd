package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;
import fu.se.swd392csms.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Attendance Management
 * Handles employee check-in/check-out and attendance tracking
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management APIs")
@CrossOrigin(origins = "*")
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    /**
     * Employee check-in
     */
    @PostMapping("/check-in/{employeeId}")
    @Operation(summary = "Check-in", description = "Employee check-in for the day")
    public ResponseEntity<AttendanceResponse> checkIn(@PathVariable Long employeeId) {
        AttendanceResponse response = attendanceService.checkIn(employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Employee check-out
     */
    @PostMapping("/check-out/{employeeId}")
    @Operation(summary = "Check-out", description = "Employee check-out for the day")
    public ResponseEntity<AttendanceResponse> checkOut(@PathVariable Long employeeId) {
        AttendanceResponse response = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get today's attendance for an employee
     */
    @GetMapping("/today/{employeeId}")
    @Operation(summary = "Get today's attendance", description = "Get attendance record for today")
    public ResponseEntity<AttendanceResponse> getTodayAttendance(@PathVariable Long employeeId) {
        AttendanceResponse response = attendanceService.getTodayAttendance(employeeId);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get attendance by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get attendance by ID", description = "Retrieve attendance details by ID")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long id) {
        AttendanceResponse response = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all attendance records for an employee
     */
    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get employee attendance", description = "Get all attendance records for an employee")
    public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(@PathVariable Long employeeId) {
        List<AttendanceResponse> responses = attendanceService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get attendance records for an employee within date range
     */
    @GetMapping("/employee/{employeeId}/range")
    @Operation(summary = "Get attendance by date range", description = "Get attendance records within a date range")
    public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendanceByDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceResponse> responses = attendanceService.getEmployeeAttendanceByDateRange(employeeId, startDate, endDate);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get all attendance records for a specific date
     */
    @GetMapping("/date/{date}")
    @Operation(summary = "Get attendance by date", description = "Get all attendance records for a specific date")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceResponse> responses = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Create or update attendance record (for managers)
     */
    @PostMapping
    @Operation(summary = "Create/Update attendance", description = "Manually create or update attendance record")
    public ResponseEntity<AttendanceResponse> createOrUpdateAttendance(@Valid @RequestBody AttendanceRequest request) {
        AttendanceResponse response = attendanceService.createOrUpdateAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Delete attendance record
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete attendance", description = "Delete an attendance record")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get total working hours for an employee in a date range
     */
    @GetMapping("/employee/{employeeId}/total-hours")
    @Operation(summary = "Get total working hours", description = "Get total working hours for an employee in a date range")
    public ResponseEntity<Double> getTotalWorkingHours(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double totalHours = attendanceService.getTotalWorkingHours(employeeId, startDate, endDate);
        return ResponseEntity.ok(totalHours);
    }
}

package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.AttendanceRequest;
import fu.se.swd392csms.dto.response.AttendanceResponse;
import fu.se.swd392csms.entity.Attendance;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.AttendanceRepository;
import fu.se.swd392csms.repository.EmployeeRepository;
import fu.se.swd392csms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AttendanceService
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    
    // Standard work hours configuration
    private static final LocalTime STANDARD_START_TIME = LocalTime.of(8, 0); // 8:00 AM
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(8, 15); // 15 minutes grace period
    private static final int STANDARD_WORK_HOURS = 8;
    
    @Override
    @Transactional
    public AttendanceResponse checkIn(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        // Check if already checked in today
        attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .ifPresent(a -> {
                    throw new BadRequestException("Employee has already checked in today");
                });
        
        // Determine status based on check-in time
        String status = now.isAfter(LATE_THRESHOLD) ? "Late" : "Present";
        
        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkInTime(now)
                .status(status)
                .build();
        
        Attendance saved = attendanceRepository.save(attendance);
        return convertToResponse(saved);
    }
    
    @Override
    @Transactional
    public AttendanceResponse checkOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new BadRequestException("No check-in record found for today"));
        
        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Employee has already checked out today");
        }
        
        attendance.setCheckOutTime(now);
        
        // Calculate working hours and overtime
        calculateWorkingHours(attendance);
        
        Attendance updated = attendanceRepository.save(attendance);
        return convertToResponse(updated);
    }
    
    @Override
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
        return convertToResponse(attendance);
    }
    
    @Override
    public List<AttendanceResponse> getEmployeeAttendance(Long employeeId) {
        List<Attendance> attendances = attendanceRepository.findByEmployeeId(employeeId);
        return attendances.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AttendanceResponse> getEmployeeAttendanceByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
        return attendances.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByDate(date);
        return attendances.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public AttendanceResponse getTodayAttendance(Long employeeId) {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .map(this::convertToResponse)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public AttendanceResponse createOrUpdateAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        LocalDate date = request.getCheckInTime().toLocalDate();
        
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), date)
                .orElse(Attendance.builder()
                        .employee(employee)
                        .date(date)
                        .build());
        
        attendance.setCheckInTime(request.getCheckInTime().toLocalTime());
        
        if (request.getCheckOutTime() != null) {
            attendance.setCheckOutTime(request.getCheckOutTime().toLocalTime());
            calculateWorkingHours(attendance);
        }
        
        attendance.setStatus(request.getStatus() != null ? request.getStatus() : "Present");
        attendance.setNotes(request.getNotes());
        
        Attendance saved = attendanceRepository.save(attendance);
        return convertToResponse(saved);
    }
    
    @Override
    @Transactional
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance", "id", id);
        }
        attendanceRepository.deleteById(id);
    }
    
    @Override
    public Double getTotalWorkingHours(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Double total = attendanceRepository.getTotalWorkingHours(employeeId, startDate, endDate);
        return total != null ? total : 0.0;
    }
    
    /**
     * Calculate working hours and overtime
     */
    private void calculateWorkingHours(Attendance attendance) {
        if (attendance.getCheckInTime() == null || attendance.getCheckOutTime() == null) {
            return;
        }
        
        Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
        double totalHours = duration.toMinutes() / 60.0;
        
        // Round to 2 decimal places
        BigDecimal workingHours = BigDecimal.valueOf(totalHours).setScale(2, RoundingMode.HALF_UP);
        attendance.setWorkingHours(workingHours);
        attendance.setTotalHours(workingHours);
        
        // Calculate overtime (hours beyond standard work hours)
        double overtime = Math.max(0, totalHours - STANDARD_WORK_HOURS);
        BigDecimal overtimeHours = BigDecimal.valueOf(overtime).setScale(2, RoundingMode.HALF_UP);
        attendance.setOvertimeHours(overtimeHours);
    }
    
    /**
     * Convert Attendance entity to AttendanceResponse DTO
     */
    private AttendanceResponse convertToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployee().getId())
                .employeeName(attendance.getEmployee().getFullName())
                .checkInTime(attendance.getCheckInTime() != null ? 
                    attendance.getDate().atTime(attendance.getCheckInTime()) : null)
                .checkOutTime(attendance.getCheckOutTime() != null ? 
                    attendance.getDate().atTime(attendance.getCheckOutTime()) : null)
                .totalHours(attendance.getTotalHours())
                .status(attendance.getStatus())
                .notes(attendance.getNotes())
                .build();
    }
}

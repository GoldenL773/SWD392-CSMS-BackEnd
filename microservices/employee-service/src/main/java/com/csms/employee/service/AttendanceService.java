package com.csms.employee.service;

import com.csms.employee.dto.AttendanceRequest;
import com.csms.employee.dto.AttendanceResponse;
import com.csms.employee.entity.Attendance;
import com.csms.employee.entity.Employee;
import com.csms.employee.exception.ResourceNotFoundException;
import com.csms.employee.exception.ValidationException;
import com.csms.employee.repository.AttendanceRepository;
import com.csms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendancesByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendancesByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public java.util.Optional<AttendanceResponse> getAttendanceByEmployeeIdAndDate(Long employeeId, LocalDate date) {
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, date).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendancesByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getDate())
                .orElse(new Attendance());

        attendance.setEmployee(employee);
        attendance.setDate(request.getDate());
        attendance.setCheckIn(request.getCheckIn());
        attendance.setCheckOut(request.getCheckOut());
        attendance.setStatus(request.getStatus().toUpperCase());

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(savedAttendance);
    }

    @Transactional
    public AttendanceResponse checkIn(Long employeeId, LocalDate date) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        LocalDate targetDate = date != null ? date : LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, targetDate)
                .orElse(new Attendance());

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        attendance.setEmployee(employee);
        attendance.setDate(targetDate);
        attendance.setCheckIn(now);

        if (now.toLocalTime().isAfter(java.time.LocalTime.of(8, 0))) {
            attendance.setStatus("LATE");
        } else {
            attendance.setStatus("EARLY");
        }

        return mapToResponse(attendanceRepository.save(attendance));
    }

    @Transactional
    public AttendanceResponse checkOut(Long employeeId, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, targetDate)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found for this date. Please check in first."));

        attendance.setCheckOut(java.time.LocalDateTime.now());

        return mapToResponse(attendanceRepository.save(attendance));
    }

    @Transactional
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    /**
     * Auto check-out at 5:00 PM for anyone who checked in but forgot to check out.
     * Runs every day at 17:05 (5 minutes after 5 PM).
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 5 17 * * *")
    @Transactional
    public void autoCheckOut() {
        LocalDate today = LocalDate.now();
        List<Attendance> activeSessions = attendanceRepository.findByDate(today).stream()
                .filter(a -> a.getCheckIn() != null && a.getCheckOut() == null)
                .collect(Collectors.toList());

        java.time.LocalDateTime fivePm = today.atTime(17, 0);
        for (Attendance a : activeSessions) {
            a.setCheckOut(fivePm);
            attendanceRepository.save(a);
        }
    }

    /**
     * Mark absent for anyone who didn't show up.
     * Runs every day at 23:55 (end of day).
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 55 23 * * *")
    @Transactional
    public void markAbsents() {
        LocalDate today = LocalDate.now();
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Attendance> todayAttendances = attendanceRepository.findByDate(today);
        java.util.Set<Long> presentEmployeeIds = todayAttendances.stream()
                .map(a -> a.getEmployee().getId())
                .collect(Collectors.toSet());

        for (Employee e : allEmployees) {
            if (!presentEmployeeIds.contains(e.getId())) {
                Attendance absent = new Attendance();
                absent.setEmployee(e);
                absent.setDate(today);
                absent.setStatus("ABSENT");
                attendanceRepository.save(absent);
            }
        }
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployee().getId())
                .employeeName(attendance.getEmployee().getFirstName() + " " + attendance.getEmployee().getLastName())
                .date(attendance.getDate())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .status(attendance.getStatus())
                .build();
    }
}

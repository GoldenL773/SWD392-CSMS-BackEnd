package com.csms.employee.controller;

import com.csms.employee.dto.AttendanceRequest;
import com.csms.employee.dto.AttendanceResponse;
import com.csms.employee.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendancesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByEmployee(employeeId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceResponse>> getAttendancesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long employeeId) {
        if (employeeId != null) {
            return attendanceService.getAttendanceByEmployeeIdAndDate(employeeId, date)
                    .map(attendance -> ResponseEntity.ok(List.of(attendance)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }
        return ResponseEntity.ok(attendanceService.getAttendancesByDate(date));
    }

    @GetMapping("/employee/{employeeId}/range")
    public ResponseEntity<List<AttendanceResponse>> getAttendancesByDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendancesByEmployeeAndDateRange(employeeId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(@Valid @RequestBody AttendanceRequest request) {
        return new ResponseEntity<>(attendanceService.recordAttendance(request), HttpStatus.CREATED);
    }

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<AttendanceResponse> checkIn(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(attendanceService.checkIn(employeeId, date), HttpStatus.CREATED);
    }

    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<AttendanceResponse> checkOut(
            @PathVariable Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.checkOut(employeeId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}

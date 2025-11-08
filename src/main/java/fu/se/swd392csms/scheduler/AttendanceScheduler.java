package fu.se.swd392csms.scheduler;

import fu.se.swd392csms.entity.Attendance;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.repository.AttendanceRepository;
import fu.se.swd392csms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Attendance Scheduler
 * Handles automatic end-of-day attendance processing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceScheduler {
    
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    
    // Configuration
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59); // 11:59 PM
    private static final LocalTime STANDARD_START_TIME = LocalTime.of(8, 0); // 8:00 AM
    private static final int STANDARD_WORK_HOURS = 8;
    
    /**
     * Auto-checkout: If an employee checked in but didn't check out, automatically check them out at end of day
     * Runs daily at 11:59 PM
     */
    @Scheduled(cron = "0 59 23 * * ?") // Every day at 11:59 PM
    @Transactional
    public void autoCheckoutEmployees() {
        log.info("Starting auto-checkout process for end of day...");
        
        try {
            LocalDate today = LocalDate.now();
            
            // Find all attendance records for today that have check-in but no check-out
            List<Attendance> attendancesWithoutCheckout = attendanceRepository.findByDate(today).stream()
                    .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() == null)
                    .toList();
            
            if (attendancesWithoutCheckout.isEmpty()) {
                log.info("No employees to auto-checkout");
                return;
            }

  /**
   * Early Absent marking: Immediately after end of working hours (5:01 PM),
   * mark employees who have not checked in today as Absent.
   * This avoids waiting until midnight and provides quicker visibility.
   * Runs daily at 17:01.
   */
  @Scheduled(cron = "0 1 17 * * ?") // Every day at 5:01 PM
  @Transactional
  public void markAbsentAfterShift() {
      log.info("Starting early absent marking (after shift) ...");

      try {
          LocalDate today = LocalDate.now();

          // Get all active employees
          List<Employee> allEmployees = employeeRepository.findAll();

          // Find employees who have already checked in today
          // (ensures we don't mark those who did check in)
          List<Long> checkedInToday = attendanceRepository.findByDate(today).stream()
                  .filter(a -> a.getCheckInTime() != null)
                  .map(a -> a.getEmployee().getId())
                  .toList();

          for (Employee employee : allEmployees) {
              if (!checkedInToday.contains(employee.getId())) {
                  // Avoid duplicate absent: ensure no attendance exists for today for this employee
                  boolean hasAnyAttendance = attendanceRepository
                          .findByEmployeeIdAndDate(employee.getId(), today)
                          .isPresent();
                  if (hasAnyAttendance) {
                      continue;
                  }

                  Attendance absentAttendance = Attendance.builder()
                          .employee(employee)
                          .date(today)
                          .status("Absent")
                          .notes("Auto-marked absent after shift - no check-in record by 17:00")
                          .build();

                  attendanceRepository.save(absentAttendance);
                  log.info("Early absent marked for employee {} (ID: {})", employee.getFullName(), employee.getId());
              }
          }

          log.info("Early absent marking finished");
      } catch (Exception e) {
          log.error("Error during early absent marking: {}", e.getMessage(), e);
      }
  }
            
            log.info("Found {} employee(s) to auto-checkout", attendancesWithoutCheckout.size());
            
            for (Attendance attendance : attendancesWithoutCheckout) {
                try {
                    // Set checkout time to end of day
                    attendance.setCheckOutTime(END_OF_DAY);
                    
                    // Calculate working hours
                    calculateWorkingHours(attendance);
                    
                    // Add note indicating auto-checkout
                    String existingNotes = attendance.getNotes() != null ? attendance.getNotes() + "; " : "";
                    attendance.setNotes(existingNotes + "Auto-checked out at end of day");
                    
                    attendanceRepository.save(attendance);
                    
                    log.info("Auto-checked out employee {} (ID: {}) at {}", 
                            attendance.getEmployee().getFullName(), 
                            attendance.getEmployee().getId(), 
                            END_OF_DAY);
                    
                } catch (Exception e) {
                    log.error("Error auto-checking out employee {} (ID: {}): {}", 
                            attendance.getEmployee().getFullName(), 
                            attendance.getEmployee().getId(), 
                            e.getMessage(), e);
                }
            }
            
            log.info("Auto-checkout process completed successfully");
            
        } catch (Exception e) {
            log.error("Error during auto-checkout process: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Auto-absent marking: If an employee didn't check in at all during the day, mark them as absent
     * Runs daily at 11:55 PM (before auto-checkout)
     */
    @Scheduled(cron = "0 55 23 * * ?") // Every day at 11:55 PM
    @Transactional
    public void markAbsentEmployees() {
        log.info("Starting auto-absent marking process...");
        
        try {
            LocalDate today = LocalDate.now();
            
            // Get all active employees
            List<Employee> allEmployees = employeeRepository.findAll();
            
            // Find employees who didn't check in today
            List<Employee> absentEmployees = allEmployees.stream()
                    .filter(emp -> {
                        boolean hasAttendanceToday = attendanceRepository.findByEmployeeIdAndDate(emp.getId(), today).isPresent();
                        return !hasAttendanceToday;
                    })
                    .toList();
            
            if (absentEmployees.isEmpty()) {
                log.info("No absent employees to mark");
                return;
            }
            
            log.info("Found {} employee(s) to mark as absent", absentEmployees.size());
            
            for (Employee employee : absentEmployees) {
                try {
                    // Create absent attendance record
                    Attendance absentAttendance = Attendance.builder()
                            .employee(employee)
                            .date(today)
                            .status("Absent")
                            .notes("Auto-marked absent - no check-in record")
                            .build();
                    
                    attendanceRepository.save(absentAttendance);
                    
                    log.info("Auto-marked employee {} (ID: {}) as absent", 
                            employee.getFullName(), 
                            employee.getId());
                    
                } catch (Exception e) {
                    log.error("Error marking employee {} (ID: {}) as absent: {}", 
                            employee.getFullName(), 
                            employee.getId(), 
                            e.getMessage(), e);
                }
            }
            
            log.info("Auto-absent marking process completed successfully");
            
        } catch (Exception e) {
            log.error("Error during auto-absent marking process: {}", e.getMessage(), e);
        }
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
}

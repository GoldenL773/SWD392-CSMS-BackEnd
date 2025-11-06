package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.SalaryResponse;
import fu.se.swd392csms.entity.Attendance;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.entity.Salary;
import fu.se.swd392csms.entity.SalaryUpdatedHistory;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.AttendanceRepository;
import fu.se.swd392csms.repository.EmployeeRepository;
import fu.se.swd392csms.repository.SalaryRepository;
import fu.se.swd392csms.repository.SalaryUpdatedHistoryRepository;
import fu.se.swd392csms.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SalaryService
 */
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {
    
    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryUpdatedHistoryRepository salaryHistoryRepository;
    
    // Salary calculation constants
    private static final BigDecimal HOURLY_RATE = new BigDecimal("50000"); // 50,000 VND per hour
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5"); // 1.5x for overtime
    private static final int STANDARD_WORK_DAYS = 22; // Standard work days per month
    private static final int STANDARD_WORK_HOURS = 8; // Standard hours per day
    
    @Override
    @Transactional
    public List<SalaryResponse> calculateMonthlySalaries(Integer month, Integer year) {
        // Get all active employees
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus()) || "Active".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
        
        List<SalaryResponse> createdSalaries = new ArrayList<>();
        
        // Calculate date range for the month
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        for (Employee employee : activeEmployees) {
            // Check if salary already exists for this employee, month, and year
            if (salaryRepository.findByEmployeeIdAndMonthAndYear(employee.getId(), month, year).isPresent()) {
                continue; // Skip if already calculated
            }
            
            // Get attendance records for the month
            List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(
                    employee.getId(), startDate, endDate);
            
            // Calculate total working hours, overtime, and count absent days
            BigDecimal totalWorkingHours = BigDecimal.ZERO;
            BigDecimal totalOvertimeHours = BigDecimal.ZERO;
            int absentDays = 0;
            
            for (Attendance attendance : attendances) {
                // Count absent days based on status
                if ("ABSENT".equalsIgnoreCase(attendance.getStatus())) {
                    absentDays++;
                }
                
                // Only count working hours for non-absent days
                if (!"ABSENT".equalsIgnoreCase(attendance.getStatus())) {
                    if (attendance.getWorkingHours() != null) {
                        totalWorkingHours = totalWorkingHours.add(attendance.getWorkingHours());
                    }
                    if (attendance.getOvertimeHours() != null) {
                        totalOvertimeHours = totalOvertimeHours.add(attendance.getOvertimeHours());
                    }
                }
            }
            
            // Calculate base salary (from employee's salary or calculated from hours)
            BigDecimal baseSalary = employee.getSalary() != null ? 
                    employee.getSalary() : 
                    calculateBaseSalaryFromHours(totalWorkingHours);
            
            // Calculate overtime pay
            BigDecimal overtimePay = totalOvertimeHours
                    .multiply(HOURLY_RATE)
                    .multiply(OVERTIME_MULTIPLIER)
                    .setScale(2, RoundingMode.HALF_UP);
            
            // Calculate deductions for absent days
            BigDecimal deduction = calculateAbsentDeduction(baseSalary, absentDays);
            
            // Calculate total salary
            BigDecimal totalSalary = baseSalary
                    .add(overtimePay)
                    .subtract(deduction)
                    .setScale(2, RoundingMode.HALF_UP);
            
            // Create salary record
            Salary salary = Salary.builder()
                    .employee(employee)
                    .month(month)
                    .year(year)
                    .baseSalary(baseSalary)
                    .bonus(overtimePay)
                    .deduction(deduction)
                    .deductions(deduction)
                    .totalSalary(totalSalary)
                    .status("Pending")
                    .notes("Auto-calculated based on attendance")
                    .build();
            
            Salary saved = salaryRepository.save(salary);
            createdSalaries.add(convertToResponse(saved));
        }
        
        return createdSalaries;
    }
    
    @Override
    public SalaryResponse getSalaryById(Long id) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary", "id", id));
        return convertToResponse(salary);
    }
    
    @Override
    public List<SalaryResponse> getEmployeeSalaries(Long employeeId) {
        List<Salary> salaries = salaryRepository.findByEmployeeId(employeeId);
        return salaries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalaryResponse> getSalariesByMonthAndYear(Integer month, Integer year) {
        List<Salary> salaries = salaryRepository.findByMonthAndYear(month, year);
        return salaries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalaryResponse> getPendingSalaries() {
        List<Salary> salaries = salaryRepository.findAllPendingSalaries();
        return salaries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SalaryResponse createOrUpdateSalary(SalaryRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        Salary salary = salaryRepository.findByEmployeeIdAndMonthAndYear(
                request.getEmployeeId(), request.getMonth(), request.getYear())
                .orElse(Salary.builder()
                        .employee(employee)
                        .month(request.getMonth())
                        .year(request.getYear())
                        .build());
        
        salary.setBaseSalary(request.getBaseSalary());
        salary.setBonus(request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO);
        salary.setDeduction(request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO);
        salary.setDeductions(request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO);
        salary.setTotalSalary(request.getTotalSalary());
        salary.setStatus(request.getStatus() != null ? request.getStatus() : "Pending");
        salary.setNotes(request.getNotes());
        
        Salary saved = salaryRepository.save(salary);
        return convertToResponse(saved);
    }
    
    @Override
    @Transactional
    public SalaryResponse updateSalaryAdjustments(Long id, BigDecimal bonus, BigDecimal deductions, Long changedBy) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary", "id", id));
        
        Employee changedByEmployee = employeeRepository.findById(changedBy)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", changedBy));
        
        // Save history before update
        SalaryUpdatedHistory history = SalaryUpdatedHistory.builder()
                .salary(salary)
                .changedBy(changedByEmployee)
                .changeDate(LocalDate.now())
                .oldBaseSalary(salary.getBaseSalary())
                .newBaseSalary(salary.getBaseSalary())
                .oldBonus(salary.getBonus())
                .newBonus(bonus)
                .oldDeduction(salary.getDeduction())
                .newDeduction(deductions)
                .oldTotalSalary(salary.getTotalSalary())
                .build();
        
        // Update salary
        salary.setBonus(bonus != null ? bonus : BigDecimal.ZERO);
        salary.setDeduction(deductions != null ? deductions : BigDecimal.ZERO);
        salary.setDeductions(deductions != null ? deductions : BigDecimal.ZERO);
        
        // Recalculate total
        BigDecimal newTotal = salary.getBaseSalary()
                .add(salary.getBonus())
                .subtract(salary.getDeduction())
                .setScale(2, RoundingMode.HALF_UP);
        salary.setTotalSalary(newTotal);
        
        history.setNewTotalSalary(newTotal);
        history.setNote("Bonus and deductions updated");
        
        salaryHistoryRepository.save(history);
        Salary updated = salaryRepository.save(salary);
        
        return convertToResponse(updated);
    }
    
    @Override
    @Transactional
    public SalaryResponse markAsPaid(Long id) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary", "id", id));
        
        if ("Paid".equals(salary.getStatus())) {
            throw new BadRequestException("Salary has already been marked as paid");
        }
        
        salary.setStatus("Paid");
        salary.setPaymentDate(LocalDateTime.now());
        
        Salary updated = salaryRepository.save(salary);
        return convertToResponse(updated);
    }
    
    @Override
    @Transactional
    public List<SalaryResponse> markMultipleAsPaid(List<Long> ids) {
        List<SalaryResponse> updated = new ArrayList<>();
        
        for (Long id : ids) {
            try {
                updated.add(markAsPaid(id));
            } catch (Exception e) {
                // Continue with other salaries if one fails
                System.err.println("Failed to mark salary " + id + " as paid: " + e.getMessage());
            }
        }
        
        return updated;
    }
    
    @Override
    @Transactional
    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salary", "id", id);
        }
        salaryRepository.deleteById(id);
    }
    
    @Override
    public Double getTotalSalaryPaid(Integer month, Integer year) {
        Double total = salaryRepository.getTotalSalaryPaid(month, year);
        return total != null ? total : 0.0;
    }
    
    @Override
    public List<SalaryResponse> getPaidSalaries() {
        List<Salary> salaries = salaryRepository.findAllPaidSalaries();
        return salaries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SalaryResponse> getPaidSalariesByPeriod(Integer month, Integer year) {
        List<Salary> salaries = salaryRepository.findPaidSalariesByMonthAndYear(month, year);
        return salaries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate base salary from working hours
     */
    private BigDecimal calculateBaseSalaryFromHours(BigDecimal totalHours) {
        return totalHours
                .multiply(HOURLY_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate deduction for absent days
     */
    private BigDecimal calculateAbsentDeduction(BigDecimal baseSalary, int absentDays) {
        if (absentDays <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal dailySalary = baseSalary.divide(
                BigDecimal.valueOf(STANDARD_WORK_DAYS), 
                2, 
                RoundingMode.HALF_UP);
        
        return dailySalary.multiply(BigDecimal.valueOf(absentDays))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Convert Salary entity to SalaryResponse DTO
     */
    private SalaryResponse convertToResponse(Salary salary) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployee().getId())
                .employeeName(salary.getEmployee().getFullName())
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

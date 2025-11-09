package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.SalaryRequest;
import fu.se.swd392csms.dto.response.SalaryResponse;
import fu.se.swd392csms.dto.response.SalaryHistoryResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Salary operations
 */
public interface SalaryService {
    
    /**
     * Calculate and create monthly salaries for all active employees
     * Based on attendance data
     * @param month Month (1-12)
     * @param year Year
     * @return List of created salary records
     */
    List<SalaryResponse> calculateMonthlySalaries(Integer month, Integer year);
    
    /**
     * Get salary by ID
     * @param id Salary ID
     * @return Salary response
     */
    SalaryResponse getSalaryById(Long id);
    
    /**
     * Get all salary records for an employee
     * @param employeeId Employee ID
     * @return List of salary records
     */
    List<SalaryResponse> getEmployeeSalaries(Long employeeId);
    
    /**
     * Get salary records for a specific month and year
     * @param month Month (1-12)
     * @param year Year
     * @return List of salary records
     */
    List<SalaryResponse> getSalariesByMonthAndYear(Integer month, Integer year);
    
    /**
     * Get all pending salaries
     * @return List of pending salary records
     */
    List<SalaryResponse> getPendingSalaries();
    
    /**
     * Create or update salary record manually
     * @param request Salary request
     * @return Salary response
     */
    SalaryResponse createOrUpdateSalary(SalaryRequest request);
    
    /**
     * Update salary bonus and deductions
     * @param id Salary ID
     * @param bonus Bonus amount
     * @param deductions Deduction amount
     * @param changedBy Employee ID who made the change
     * @return Updated salary response
     */
    SalaryResponse updateSalaryAdjustments(Long id, BigDecimal bonus, BigDecimal deductions, Long changedBy, String note);
    
    /**
     * Mark salary as paid
     * @param id Salary ID
     * @return Updated salary response
     */
    SalaryResponse markAsPaid(Long id);
    
    /**
     * Mark multiple salaries as paid
     * @param ids List of salary IDs
     * @return List of updated salary responses
     */
    List<SalaryResponse> markMultipleAsPaid(List<Long> ids);
    
    /**
     * Delete salary record
     * @param id Salary ID
     */
    void deleteSalary(Long id);
    
    /**
     * Get total salary paid for a month and year
     * @param month Month
     * @param year Year
     * @return Total salary amount
     */
    Double getTotalSalaryPaid(Integer month, Integer year);
    
    /**
     * Get all paid salaries
     * @return List of paid salary records
     */
    List<SalaryResponse> getPaidSalaries();
    
    /**
     * Get paid salaries for a specific month and year
     * @param month Month (1-12)
     * @param year Year
     * @return List of paid salary records
     */
    List<SalaryResponse> getPaidSalariesByPeriod(Integer month, Integer year);

    /**
     * Get update history for a salary record
     * @param salaryId Salary ID
     * @return List of history items ordered by newest first
     */
    List<SalaryHistoryResponse> getSalaryHistory(Long salaryId);
}

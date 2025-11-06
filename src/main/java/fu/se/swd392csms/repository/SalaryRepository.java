package fu.se.swd392csms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fu.se.swd392csms.entity.Salary;

/**
 * Repository interface for Salary entity
 * Provides CRUD operations and custom queries for salary records
 */
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    
    /**
     * Find all salary records for an employee
     * @param employeeId Employee ID
     * @return List of salary records
     */
    List<Salary> findByEmployeeId(Long employeeId);

    /**
     * Find all salary records for an employee and year
     * @param employeeId Employee ID
     * @param year Year
     * @return List of salary records
     */
    List<Salary> findByEmployeeIdAndYear(Long employeeId, Integer year);

    /**
     * Find salary by employee, month, and year
     * @param employeeId Employee ID
     * @param month Month (1-12)
     * @param year Year
     * @return Optional containing the salary record if found
     */
    Optional<Salary> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    /**
     * Find all salaries for a specific month and year
     * @param month Month (1-12)
     * @param year Year
     * @return List of salary records
     */
    List<Salary> findByMonthAndYear(Integer month, Integer year);
    
    /**
     * Find salaries by status
     * @param status Salary status (Pending, Paid)
     * @return List of salary records
     */
    List<Salary> findByStatus(String status);
    
    /**
     * Find salaries by employee and status
     * @param employeeId Employee ID
     * @param status Salary status
     * @return List of salary records
     */
    List<Salary> findByEmployeeIdAndStatus(Long employeeId, String status);
    
    /**
     * Find all pending salaries
     * @return List of pending salary records
     */
    @Query("SELECT s FROM Salary s WHERE s.status = 'Pending' ORDER BY s.year DESC, s.month DESC")
    List<Salary> findAllPendingSalaries();
    
    /**
     * Get total salary paid for a month and year
     * @param month Month
     * @param year Year
     * @return Total salary amount
     */
    @Query("SELECT SUM(s.totalSalary) FROM Salary s WHERE s.month = :month AND s.year = :year AND s.status = 'Paid'")
    Double getTotalSalaryPaid(Integer month, Integer year);
}

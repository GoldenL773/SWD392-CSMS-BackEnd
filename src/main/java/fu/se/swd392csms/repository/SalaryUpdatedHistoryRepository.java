package fu.se.swd392csms.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fu.se.swd392csms.entity.SalaryUpdatedHistory;

/**
 * Repository interface for SalaryUpdatedHistory entity
 * Provides CRUD operations and custom queries for salary update history
 */
@Repository
public interface SalaryUpdatedHistoryRepository extends JpaRepository<SalaryUpdatedHistory, Long> {
    
    /**
     * Find all history records for a salary
     * @param salaryId Salary ID
     * @return List of history records ordered by date descending
     */
    @Query("SELECT h FROM SalaryUpdatedHistory h WHERE h.salary.id = :salaryId ORDER BY h.changeDate DESC")
    List<SalaryUpdatedHistory> findBySalaryId(@Param("salaryId") Long salaryId);
    
    /**
     * Find all changes made by an employee
     * @param employeeId Employee ID (who made the change)
     * @return List of history records
     */
    List<SalaryUpdatedHistory> findByChangedById(Long employeeId);
    
    /**
     * Find history records within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of history records
     */
    @Query("SELECT h FROM SalaryUpdatedHistory h WHERE h.changeDate BETWEEN :startDate AND :endDate ORDER BY h.changeDate DESC")
    List<SalaryUpdatedHistory> findByChangeDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get all salary update history ordered by date
     * @return List of all history records
     */
    @Query("SELECT h FROM SalaryUpdatedHistory h ORDER BY h.changeDate DESC")
    List<SalaryUpdatedHistory> findAllOrderByDateDesc();
}

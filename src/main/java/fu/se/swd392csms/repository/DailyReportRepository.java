package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DailyReport entity
 * Provides CRUD operations and custom queries for daily reports
 */
@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    
    /**
     * Find report by date
     * @param reportDate Report date
     * @return Optional containing the report if found
     */
    Optional<DailyReport> findByReportDate(LocalDate reportDate);
    
    /**
     * Find reports within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of reports ordered by date descending
     */
    @Query("SELECT r FROM DailyReport r WHERE r.reportDate BETWEEN :startDate AND :endDate ORDER BY r.reportDate DESC")
    List<DailyReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find reports created by an employee
     * @param employeeId Employee ID
     * @return List of reports
     */
    List<DailyReport> findByCreatedById(Long employeeId);
    
    /**
     * Check if report exists for a date
     * @param reportDate Report date
     * @return true if report exists
     */
    boolean existsByReportDate(LocalDate reportDate);
    
    /**
     * Get all reports ordered by date descending
     * @return List of all reports
     */
    @Query("SELECT r FROM DailyReport r ORDER BY r.reportDate DESC")
    List<DailyReport> findAllOrderByDateDesc();
    
    /**
     * Get latest N reports
     * @return List of latest reports
     */
    @Query("SELECT r FROM DailyReport r ORDER BY r.reportDate DESC")
    List<DailyReport> findLatestReports();
}

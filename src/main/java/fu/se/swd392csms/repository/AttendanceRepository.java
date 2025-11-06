package fu.se.swd392csms.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fu.se.swd392csms.entity.Attendance;

/**
 * Repository interface for Attendance entity
 * Provides CRUD operations and custom queries for attendance records
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    /**
     * Find all attendance records for an employee
     * @param employeeId Employee ID
     * @return List of attendance records
     */
    List<Attendance> findByEmployeeId(Long employeeId);
    
    /**
     * Find attendance by employee and date
     * @param employeeId Employee ID
     * @param date Date
     * @return Optional containing the attendance record if found
     */
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    
    /**
     * Find attendance records for an employee within date range
     * @param employeeId Employee ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of attendance records
     */
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC")
    List<Attendance> findByEmployeeIdAndDateBetween(@Param("employeeId") Long employeeId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * Find attendance records for an employee within check-in time range
     * Note: This method uses date field for filtering since checkInTime is LocalTime
     * @param employeeId Employee ID
     * @param startDateTime Start date time
     * @param endDateTime End date time
     * @return List of attendance records
     */
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC")
    List<Attendance> findByEmployeeIdAndCheckInTimeBetween(@Param("employeeId") Long employeeId,
                                                            @Param("startDate") LocalDate startDateTime,
                                                            @Param("endDate") LocalDate endDateTime);

    /**
     * Find all attendance records for a specific date
     * @param date Date
     * @return List of attendance records
     */
    List<Attendance> findByDate(LocalDate date);
    
    /**
     * Find attendance records by status
     * @param status Attendance status (Present, Absent, Late)
     * @return List of attendance records
     */
    List<Attendance> findByStatus(String status);
    
    /**
     * Find attendance records by employee and status
     * @param employeeId Employee ID
     * @param status Attendance status
     * @return List of attendance records
     */
    List<Attendance> findByEmployeeIdAndStatus(Long employeeId, String status);
    
    /**
     * Get total working hours for an employee in a date range
     * @param employeeId Employee ID
     * @param startDate Start date
     * @param endDate End date
     * @return Total working hours
     */
    @Query("SELECT SUM(a.workingHours) FROM Attendance a WHERE a.employee.id = :employeeId AND a.date BETWEEN :startDate AND :endDate")
    Double getTotalWorkingHours(@Param("employeeId") Long employeeId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}

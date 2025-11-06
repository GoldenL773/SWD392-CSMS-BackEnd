package fu.se.swd392csms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fu.se.swd392csms.entity.Employee;

/**
 * Repository interface for Employee entity
 * Provides CRUD operations and custom queries for employees
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Find all employees by status
     * @param status Employee status (Active, Inactive)
     * @return List of employees with the given status
     */
    List<Employee> findByStatus(String status);
    
    /**
     * Find all employees by position
     * @param position Employee position (Manager, Barista, Cashier, etc.)
     * @return List of employees with the given position
     */
    List<Employee> findByPosition(String position);
    
    /**
     * Find employee by phone number
     * @param phone Phone number
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByPhone(String phone);

    /**
     * Find employee by email
     * @param email Email address
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if employee exists by email
     * @param email Email address
     * @return true if employee exists with the given email
     */
    boolean existsByEmail(String email);

    /**
     * Find employee by user ID
     * @param userId User ID
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByUserId(Long userId);

    /**
     * Find all active employees
     * @return List of active employees
     */
    @Query("SELECT e FROM Employee e WHERE e.status = 'Active' ORDER BY e.fullName")
    List<Employee> findAllActiveEmployees();

    /**
     * Search employees by name (case-insensitive)
     * @param name Name to search
     * @return List of matching employees
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> searchByName(String name);
}

package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity
 * Provides CRUD operations and custom queries for orders
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders by status
     * @param status Order status (Pending, Preparing, Completed, Cancelled)
     * @return List of orders with the given status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Find all orders by status with pagination
     * @param status Order status
     * @param pageable Pagination parameters
     * @return Page of orders with the given status
     */
    Page<Order> findByStatus(String status, Pageable pageable);
    
    /**
     * Find all orders by employee
     * @param employeeId Employee ID
     * @return List of orders created by the employee
     */
    List<Order> findByEmployeeId(Long employeeId);
    
    /**
     * Find orders within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders within the date range
     */
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders by employee and date range
     * @param employeeId Employee ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of matching orders
     */
    @Query("SELECT o FROM Order o WHERE o.employee.id = :employeeId AND o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders by status and date range
     * @param status Order status
     * @param startDate Start date
     * @param endDate End date
     * @return List of matching orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findByStatusAndDateRange(@Param("status") String status,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find all orders ordered by date descending
     * @return List of all orders sorted by date
     */
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByDateDesc();
    
    /**
     * Get total revenue for a date range
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'Completed' AND o.orderDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenue(@Param("startDate") LocalDateTime startDate, 
                          @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count orders by status
     * @param status Order status
     * @return Number of orders with the status
     */
    long countByStatus(String status);

    /**
     * Find pending orders older than the provided threshold (optimized filtering at DB level)
     * @param threshold LocalDateTime threshold; orders with orderDate before this are returned
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.orderDate < :threshold")
    List<Order> findPendingOrdersOlderThan(@Param("threshold") LocalDateTime threshold);
}

package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity
 * Provides CRUD operations and custom queries for order items
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Find all items for an order
     * @param orderId Order ID
     * @return List of order items
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Find all orders containing a specific product
     * @param productId Product ID
     * @return List of order items
     */
    List<OrderItem> findByProductId(Long productId);
    
    /**
     * Delete all items for an order
     * @param orderId Order ID
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * Get order items with product details
     * @param orderId Order ID
     * @return List of order items with product information
     */
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithProduct(Long orderId);
}

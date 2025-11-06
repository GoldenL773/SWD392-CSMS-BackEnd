package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.CreateOrderRequest;
import fu.se.swd392csms.dto.request.UpdateOrderStatusRequest;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Order Service Interface
 * Handles business logic for order management
 */
public interface OrderService {
    
    /**
     * Create a new order
     * @param request Order creation request
     * @return Created order response
     */
    OrderResponse createOrder(CreateOrderRequest request);
    
    /**
     * Get order by ID
     * @param id Order ID
     * @return Order response
     */
    OrderResponse getOrderById(Long id);
    
    /**
     * Get all orders with optional filtering and pagination
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    Page<OrderResponse> getAllOrders(String status, Pageable pageable);
    
    /**
     * Update order status
     * @param id Order ID
     * @param request Status update request
     * @return Updated order response
     */
    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);
    
    /**
     * Delete order
     * @param id Order ID
     * @return Success message
     */
    MessageResponse deleteOrder(Long id);
}

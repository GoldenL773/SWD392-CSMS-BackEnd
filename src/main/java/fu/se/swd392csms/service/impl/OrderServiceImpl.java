package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.CreateOrderRequest;
import fu.se.swd392csms.dto.request.OrderItemRequest;
import fu.se.swd392csms.dto.request.UpdateOrderStatusRequest;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.OrderItemResponse;
import fu.se.swd392csms.dto.response.OrderResponse;
import fu.se.swd392csms.entity.*;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.*;
import fu.se.swd392csms.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service Implementation
 * Handles business logic for order management
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductIngredientRepository productIngredientRepository;
    
    /**
     * Create a new order
     * @param request Order creation request
     * @return Created order response
     */
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        // Create order entity
        Order order = new Order();
        order.setEmployee(employee);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        
        // Calculate total amount and process order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Validate product exists
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemRequest.getProductId()));
            
            // Check if product is available
            if (!"Available".equalsIgnoreCase(product.getStatus())) {
                throw new BadRequestException("Product '" + product.getName() + "' is not available");
            }
            
            // Get product ingredients
            List<ProductIngredient> productIngredients = productIngredientRepository
                    .findByProductId(itemRequest.getProductId());
            
            // Check and reduce ingredient stock
            for (ProductIngredient pi : productIngredients) {
                Ingredient ingredient = pi.getIngredient();
                BigDecimal requiredQuantity = pi.getQuantityRequired()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                
                if (ingredient.getQuantity().compareTo(requiredQuantity) < 0) {
                    throw new BadRequestException(
                            "Insufficient stock for ingredient '" + ingredient.getName() + 
                            "'. Required: " + requiredQuantity + ", Available: " + ingredient.getQuantity()
                    );
                }
                
                // Reduce ingredient stock
                ingredient.setQuantity(ingredient.getQuantity().subtract(requiredQuantity));
                ingredientRepository.save(ingredient);
            }
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            
            // Calculate subtotal
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
            
            orderItems.add(orderItem);
        }
        
        // Set total amount and save order
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        // Save order items
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        
        // Convert to response
        return convertToOrderResponse(savedOrder, orderItems);
    }
    
    /**
     * Get order by ID
     * @param id Order ID
     * @return Order response
     */
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        
        return convertToOrderResponse(order, orderItems);
    }
    
    /**
     * Get all orders with optional filtering and pagination
     * @param status Optional status filter
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    @Override
    public Page<OrderResponse> getAllOrders(String status, Pageable pageable) {
        Page<Order> orderPage;
        
        if (status != null && !status.isEmpty()) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }
        
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return convertToOrderResponse(order, items);
                })
                .collect(Collectors.toList());
        
        return new PageImpl<>(orderResponses, pageable, orderPage.getTotalElements());
    }
    
    /**
     * Update order status
     * @param id Order ID
     * @param request Status update request
     * @return Updated order response
     */
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        // Validate status
        String newStatus = request.getStatus().toUpperCase();
        if (!newStatus.matches("PENDING|PROCESSING|COMPLETED|CANCELLED")) {
            throw new BadRequestException("Invalid status. Must be one of: PENDING, PROCESSING, COMPLETED, CANCELLED");
        }
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        
        return convertToOrderResponse(updatedOrder, orderItems);
    }
    
    /**
     * Delete order
     * @param id Order ID
     * @return Success message
     */
    @Override
    @Transactional
    public MessageResponse deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        // Delete order items first
        orderItemRepository.deleteByOrderId(id);
        
        // Delete order
        orderRepository.delete(order);
        
        return new MessageResponse("Order deleted successfully");
    }
    
    /**
     * Convert Order entity to OrderResponse DTO
     * @param order Order entity
     * @param orderItems List of order items
     * @return Order response DTO
     */
    private OrderResponse convertToOrderResponse(Order order, List<OrderItem> orderItems) {
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .employeeId(order.getEmployee().getId())
                .employeeName(order.getEmployee().getFullName())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemResponses)
                .build();
    }
}

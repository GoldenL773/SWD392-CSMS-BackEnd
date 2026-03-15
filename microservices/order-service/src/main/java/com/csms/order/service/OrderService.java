package com.csms.order.service;

import com.csms.order.dto.OrderItemRequest;
import com.csms.order.dto.OrderItemResponse;
import com.csms.order.dto.OrderRequest;
import com.csms.order.dto.OrderResponse;
import com.csms.order.entity.Order;
import com.csms.order.entity.OrderItem;
import com.csms.order.exception.ResourceNotFoundException;
import com.csms.order.repository.OrderRepository;
import com.csms.order.saga.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSagaOrchestrator sagaOrchestrator;

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderResponse> getAllOrders(
            String status, LocalDateTime startDate, LocalDateTime endDate, org.springframework.data.domain.Pageable pageable) {
        
        String filterStatus = (status == null || status.equalsIgnoreCase("ALL")) ? null : status.toUpperCase();
        
        return orderRepository.findByFilters(filterStatus, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderResponse> getOrdersByUser(Long userId, org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    // Creating an order is not just saving to DB, it involves a saga to deduct inventory
    public OrderResponse createOrder(OrderRequest request) {
        Order order = Order.builder()
                .userId(request.getUserId())
                .orderDate(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime())
                .status("PENDING")
                .note(request.getNote())
                .employeeName(request.getEmployeeName())
                .totalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : java.math.BigDecimal.ZERO)
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            java.math.BigDecimal unitPrice = itemRequest.getPrice() != null ? itemRequest.getPrice() : java.math.BigDecimal.ZERO;
            OrderItem item = OrderItem.builder()
                    .productId(itemRequest.getProductId())
                    .variantId(itemRequest.getVariantId())
                    .comboId(itemRequest.getComboId())
                    .productName("Pending...") // Will be updated by Saga
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(unitPrice.multiply(java.math.BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        
        // Trigger Saga
        sagaOrchestrator.processOrder(savedOrder);
        
        // Return updated state
        return mapToResponse(orderRepository.findById(savedOrder.getId()).get());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(status.toUpperCase());
        return mapToResponse(orderRepository.save(order));
    }

    // Cancellation involves compensation
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        sagaOrchestrator.cancelOrder(order);
        
        return mapToResponse(orderRepository.findById(id).get());
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .variantId(item.getVariantId())
                        .comboId(item.getComboId())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .employeeName(order.getEmployeeName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

package com.csms.order.controller;

import com.csms.order.dto.OrderRequest;
import com.csms.order.dto.OrderResponse;
import com.csms.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<OrderResponse>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            // Backward-compatible alias used by finance modal
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            org.springframework.data.domain.Pageable pageable) {
        Long effectiveUserId = userId != null ? userId : employeeId;
        LocalDateTime parsedStartDate = parseDateTimeParam(startDate, true);
        LocalDateTime parsedEndDate = parseDateTimeParam(endDate, false);
        return ResponseEntity.ok(orderService.getAllOrders(status, effectiveUserId, parsedStartDate, parsedEndDate, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<org.springframework.data.domain.Page<OrderResponse>> getOrdersByUser(
            @PathVariable Long userId,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        if (request.getUserId() == null && headerUserId != null) {
            request.setUserId(headerUserId);
        }
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    private LocalDateTime parseDateTimeParam(String value, boolean isStart) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ignored) {
            LocalDate date = LocalDate.parse(value);
            return isStart ? date.atStartOfDay() : date.atTime(23, 59, 59);
        }
    }
}

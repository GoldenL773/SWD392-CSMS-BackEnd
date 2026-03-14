package com.csms.report.client;

import com.csms.report.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderClient {
    
    @GetMapping("/api/orders")
    List<OrderResponse> getAllOrders();
}

package com.csms.order.client;

import com.csms.order.dto.ComboResponse;
import com.csms.order.dto.ProductResponse;
import com.csms.order.dto.PromotionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);

    @GetMapping("/api/combos/{id}")
    ComboResponse getComboById(@PathVariable("id") Long id);

    @GetMapping("/api/promotions/{id}")
    PromotionResponse getPromotionById(@PathVariable("id") Long id);
}

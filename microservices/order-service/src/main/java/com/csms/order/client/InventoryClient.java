package com.csms.order.client;

import com.csms.order.dto.InventoryTransactionRequest;
import com.csms.order.dto.ProductIngredientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    
    @GetMapping("/api/product-ingredients/product/{productId}")
    List<ProductIngredientResponse> getIngredientsByProductId(@PathVariable("productId") Long productId);

    @PostMapping("/api/transactions")
    Object recordTransaction(@RequestBody InventoryTransactionRequest request);
}

package com.csms.product.client;

import com.csms.product.dto.IngredientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/ingredients/all")
    List<IngredientResponse> getAllIngredients();

    @GetMapping("/api/ingredients/{id}")
    IngredientResponse getIngredientById(@PathVariable("id") Long id);
}

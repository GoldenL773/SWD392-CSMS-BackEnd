package com.csms.inventory.controller;

import com.csms.inventory.dto.ProductIngredientRequest;
import com.csms.inventory.dto.ProductIngredientResponse;
import com.csms.inventory.service.ProductIngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-ingredients")
@RequiredArgsConstructor
public class ProductIngredientController {

    private final ProductIngredientService productIngredientService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductIngredientResponse>> getIngredientsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productIngredientService.getIngredientsByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ProductIngredientResponse> addIngredientToProduct(@Valid @RequestBody ProductIngredientRequest request) {
        return new ResponseEntity<>(productIngredientService.addIngredientToProduct(request), HttpStatus.CREATED);
    }

    @PutMapping("/product/{productId}/ingredient/{ingredientId}")
    public ResponseEntity<ProductIngredientResponse> updateIngredientInProduct(
            @PathVariable Long productId,
            @PathVariable Long ingredientId,
            @Valid @RequestBody ProductIngredientRequest request) {
        return ResponseEntity.ok(productIngredientService.updateIngredientInProduct(productId, ingredientId, request));
    }

    @DeleteMapping("/product/{productId}/ingredient/{ingredientId}")
    public ResponseEntity<Void> removeIngredientFromProduct(
            @PathVariable Long productId,
            @PathVariable Long ingredientId) {
        productIngredientService.removeIngredientFromProduct(productId, ingredientId);
        return ResponseEntity.noContent().build();
    }
}

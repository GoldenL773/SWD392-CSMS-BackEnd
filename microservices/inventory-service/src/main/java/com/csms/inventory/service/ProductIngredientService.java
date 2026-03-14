package com.csms.inventory.service;

import com.csms.inventory.dto.ProductIngredientRequest;
import com.csms.inventory.dto.ProductIngredientResponse;
import com.csms.inventory.entity.Ingredient;
import com.csms.inventory.entity.ProductIngredient;
import com.csms.inventory.exception.ResourceNotFoundException;
import com.csms.inventory.exception.ValidationException;
import com.csms.inventory.repository.IngredientRepository;
import com.csms.inventory.repository.ProductIngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductIngredientService {

    private final ProductIngredientRepository productIngredientRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public List<ProductIngredientResponse> getIngredientsByProductId(Long productId) {
        return productIngredientRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductIngredientResponse addIngredientToProduct(ProductIngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + request.getIngredientId()));

        if (productIngredientRepository.findByProductIdAndIngredientId(request.getProductId(), request.getIngredientId()).isPresent()) {
            throw new ValidationException("Ingredient already added to product");
        }

        ProductIngredient productIngredient = ProductIngredient.builder()
                .productId(request.getProductId())
                .ingredient(ingredient)
                .quantity(request.getQuantity())
                .build();

        ProductIngredient saved = productIngredientRepository.save(productIngredient);
        return mapToResponse(saved);
    }

    @Transactional
    public ProductIngredientResponse updateIngredientInProduct(Long productId, Long ingredientId, ProductIngredientRequest request) {
        ProductIngredient productIngredient = productIngredientRepository.findByProductIdAndIngredientId(productId, ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found for product id: " + productId));

        if (!request.getProductId().equals(productId) || !request.getIngredientId().equals(ingredientId)) {
            throw new ValidationException("Cannot change product or ingredient IDs");
        }

        productIngredient.setQuantity(request.getQuantity());

        ProductIngredient updated = productIngredientRepository.save(productIngredient);
        return mapToResponse(updated);
    }

    @Transactional
    public void removeIngredientFromProduct(Long productId, Long ingredientId) {
        ProductIngredient productIngredient = productIngredientRepository.findByProductIdAndIngredientId(productId, ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found for product id: " + productId));

        productIngredientRepository.delete(productIngredient);
    }

    private ProductIngredientResponse mapToResponse(ProductIngredient pi) {
        return ProductIngredientResponse.builder()
                .id(pi.getId())
                .productId(pi.getProductId())
                .ingredientId(pi.getIngredient().getId())
                .ingredientName(pi.getIngredient().getName())
                .unit(pi.getIngredient().getUnit())
                .quantity(pi.getQuantity())
                .build();
    }
}

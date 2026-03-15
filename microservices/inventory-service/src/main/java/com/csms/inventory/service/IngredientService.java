package com.csms.inventory.service;

import com.csms.inventory.dto.IngredientRequest;
import com.csms.inventory.dto.IngredientResponse;
import com.csms.inventory.entity.Ingredient;
import com.csms.inventory.exception.ResourceNotFoundException;
import com.csms.inventory.exception.ValidationException;
import com.csms.inventory.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<IngredientResponse> getAllIngredients(org.springframework.data.domain.Pageable pageable) {
        return ingredientRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<IngredientResponse> getAllIngredientsList() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        System.out.println("InventoryService: Found " + ingredients.size() + " ingredients in repository");
        return ingredients.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<IngredientResponse> searchIngredients(String keyword, org.springframework.data.domain.Pageable pageable) {
        return ingredientRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public IngredientResponse getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
        return mapToResponse(ingredient);
    }

    @Transactional(readOnly = true)
    public List<IngredientResponse> getLowStockIngredients() {
        // Find ingredients where current stock is less than or equal to min stock
        return ingredientRepository.findAll().stream()
                .filter(i -> i.getCurrentStock().compareTo(i.getMinStock()) <= 0)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        if (ingredientRepository.existsByName(request.getName())) {
            throw new ValidationException("Ingredient name already exists");
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock() : BigDecimal.ZERO)
                .minStock(request.getMinStock() != null ? request.getMinStock() : BigDecimal.ZERO)
                .unitCost(request.getUnitCost())
                .build();

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return mapToResponse(savedIngredient);
    }

    @Transactional
    public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        if (!ingredient.getName().equals(request.getName()) && ingredientRepository.existsByName(request.getName())) {
            throw new ValidationException("Ingredient name already exists");
        }

        ingredient.setName(request.getName());
        ingredient.setUnit(request.getUnit());
        ingredient.setMinStock(request.getMinStock());
        ingredient.setUnitCost(request.getUnitCost());
        
        // Note: currentStock is usually updated via transactions, not direct update
        // but for administrative purposes, it might be allowed here.
        if (request.getCurrentStock() != null) {
            ingredient.setCurrentStock(request.getCurrentStock());
        }

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return mapToResponse(updatedIngredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient not found with id: " + id);
        }
        ingredientRepository.deleteById(id);
    }

    private IngredientResponse mapToResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .currentStock(ingredient.getCurrentStock())
                .minStock(ingredient.getMinStock())
                .unitCost(ingredient.getUnitCost())
                .createdAt(ingredient.getCreatedAt())
                .updatedAt(ingredient.getUpdatedAt())
                .build();
    }
}

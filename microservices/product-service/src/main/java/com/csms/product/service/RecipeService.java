package com.csms.product.service;

import com.csms.product.dto.RecipeRequest;
import com.csms.product.dto.RecipeResponse;
import com.csms.product.entity.Recipe;
import com.csms.product.entity.RecipeIngredient;
import com.csms.product.exception.ResourceNotFoundException;
import com.csms.product.exception.ValidationException;
import com.csms.product.repository.ProductRepository;
import com.csms.product.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final com.csms.product.client.InventoryClient inventoryClient;

    @Transactional(readOnly = true)
    public List<RecipeResponse> getAllRecipes() {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return recipeRepository.findAll().stream()
                .map(r -> mapToResponse(r, ingredientData))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponse> getRecipesByProductId(Long productId) {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return recipeRepository.findByProductId(productId).stream()
                .map(r -> mapToResponse(r, ingredientData))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        return mapToResponse(recipe, fetchIngredientData());
    }

    public RecipeResponse getRecipeByProductIdSingle(Long productId) {
        Recipe recipe = recipeRepository.findByProductId(productId)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found for product: " + productId));
        return mapToResponse(recipe, fetchIngredientData());
    }

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        if (!productRepository.existsById(request.getProductId())) {
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        // Check for existing recipe to avoid duplicate productId error
        java.util.Optional<Recipe> existingRecipe = recipeRepository.findByProductId(request.getProductId()).stream().findFirst();
        if (existingRecipe.isPresent()) {
            log.info("Recipe already exists for product {}, redirecting to update.", request.getProductId());
            return updateRecipe(existingRecipe.get().getId(), request);
        }

        Recipe recipe = Recipe.builder()
                .productId(request.getProductId())
                .instructions(request.getInstructions())
                .prepTime(request.getPrepTime())
                .build();

        List<RecipeRequest.IngredientRequest> normalizedIngredients = normalizeIngredients(request.getIngredients());
        List<RecipeIngredient> ingredients = normalizedIngredients.stream()
                .map(ingReq -> RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredientId(ingReq.getIngredientId())
                        .quantity(ingReq.getQuantity())
                        .unit(ingReq.getUnit())
                        .build())
                .collect(Collectors.toList());

        recipe.setIngredients(ingredients);

        return mapToResponse(recipeRepository.save(recipe), fetchIngredientData());
    }

    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        recipe.setInstructions(request.getInstructions());
        recipe.setPrepTime(request.getPrepTime());

        // Update ingredients
        recipe.getIngredients().clear();
        List<RecipeRequest.IngredientRequest> normalizedIngredients = normalizeIngredients(request.getIngredients());
        List<RecipeIngredient> newIngredients = normalizedIngredients.stream()
                .map(ingReq -> RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredientId(ingReq.getIngredientId())
                        .quantity(ingReq.getQuantity())
                        .unit(ingReq.getUnit())
                        .build())
                .collect(Collectors.toList());
        recipe.getIngredients().addAll(newIngredients);

        return mapToResponse(recipeRepository.save(recipe), fetchIngredientData());
    }

    private List<RecipeRequest.IngredientRequest> normalizeIngredients(List<RecipeRequest.IngredientRequest> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new ValidationException("At least one ingredient is required");
        }

        LinkedHashMap<Long, RecipeRequest.IngredientRequest> merged = new LinkedHashMap<>();
        for (RecipeRequest.IngredientRequest item : ingredients) {
            if (item == null || item.getIngredientId() == null) {
                continue;
            }
            if (item.getQuantity() == null || item.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                continue;
            }

            RecipeRequest.IngredientRequest existing = merged.get(item.getIngredientId());
            if (existing == null) {
                RecipeRequest.IngredientRequest copy = new RecipeRequest.IngredientRequest();
                copy.setIngredientId(item.getIngredientId());
                copy.setQuantity(item.getQuantity());
                copy.setUnit(item.getUnit());
                merged.put(item.getIngredientId(), copy);
            } else {
                existing.setQuantity(existing.getQuantity().add(item.getQuantity()));
                if ((existing.getUnit() == null || existing.getUnit().isBlank()) && item.getUnit() != null && !item.getUnit().isBlank()) {
                    existing.setUnit(item.getUnit());
                }
            }
        }

        if (merged.isEmpty()) {
            throw new ValidationException("At least one valid ingredient is required");
        }

        return new ArrayList<>(merged.values());
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
    }

    private java.util.Map<Long, com.csms.product.dto.IngredientResponse> fetchIngredientData() {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientMap = new java.util.HashMap<>();
        try {
            java.util.List<com.csms.product.dto.IngredientResponse> ingredients = inventoryClient.getAllIngredients();
            if (ingredients != null) {
                log.info("RecipeService: Fetched {} ingredients from inventory-service", ingredients.size());
                ingredients.forEach(i -> {
                    if (i.getId() != null) {
                        ingredientMap.put(i.getId(), i);
                    }
                });
            } else {
                log.warn("RecipeService: Inventory service returned null list of ingredients");
            }
        } catch (Exception e) {
            log.error("RecipeService: Failed to fetch ingredient data: {}", e.getMessage());
        }
        return ingredientMap;
    }

    private RecipeResponse mapToResponse(Recipe recipe, java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData) {
        String productName = productRepository.findById(recipe.getProductId())
                .map(p -> p.getName())
                .orElse("Unknown Product");

        List<RecipeResponse.IngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                .map(ing -> {
                    com.csms.product.dto.IngredientResponse data = ingredientData.get(ing.getIngredientId());
                    String name = data != null ? data.getName() : "Ingredient " + ing.getIngredientId();
                    java.math.BigDecimal stock = data != null ? data.getCurrentStock() : java.math.BigDecimal.ZERO;
                    
                    return RecipeResponse.IngredientResponse.builder()
                        .ingredientId(ing.getIngredientId())
                        .ingredientName(name)
                        .quantity(ing.getQuantity())
                        .unit(ing.getUnit())
                        .currentStock(stock)
                        .build();
                })
                .collect(Collectors.toList());

        return RecipeResponse.builder()
                .id(recipe.getId())
                .productId(recipe.getProductId())
                .productName(productName)
                .instructions(recipe.getInstructions())
                .prepTime(recipe.getPrepTime())
                .ingredients(ingredientResponses)
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }
}

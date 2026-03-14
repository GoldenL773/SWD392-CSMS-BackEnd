package com.csms.product.service;

import com.csms.product.dto.RecipeRequest;
import com.csms.product.dto.RecipeResponse;
import com.csms.product.entity.Recipe;
import com.csms.product.entity.RecipeIngredient;
import com.csms.product.exception.ResourceNotFoundException;
import com.csms.product.repository.ProductRepository;
import com.csms.product.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<RecipeResponse> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipeResponse getRecipesByProductId(Long productId) {
        Recipe recipe = recipeRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found for product id: " + productId));
        return mapToResponse(recipe);
    }

    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        return mapToResponse(recipe);
    }

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        if (!productRepository.existsById(request.getProductId())) {
            throw new ResourceNotFoundException("Product not found with id: " + request.getProductId());
        }

        Recipe recipe = Recipe.builder()
                .productId(request.getProductId())
                .instructions(request.getInstructions())
                .prepTime(request.getPrepTime())
                .build();

        List<RecipeIngredient> ingredients = request.getIngredients().stream()
                .map(ingReq -> RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredientId(ingReq.getIngredientId())
                        .quantity(ingReq.getQuantity())
                        .unit(ingReq.getUnit())
                        .build())
                .collect(Collectors.toList());

        recipe.setIngredients(ingredients);

        return mapToResponse(recipeRepository.save(recipe));
    }

    @Transactional
    public RecipeResponse updateRecipe(Long id, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        recipe.setInstructions(request.getInstructions());
        recipe.setPrepTime(request.getPrepTime());

        // Update ingredients
        recipe.getIngredients().clear();
        List<RecipeIngredient> newIngredients = request.getIngredients().stream()
                .map(ingReq -> RecipeIngredient.builder()
                        .recipe(recipe)
                        .ingredientId(ingReq.getIngredientId())
                        .quantity(ingReq.getQuantity())
                        .unit(ingReq.getUnit())
                        .build())
                .collect(Collectors.toList());
        recipe.getIngredients().addAll(newIngredients);

        return mapToResponse(recipeRepository.save(recipe));
    }

    @Transactional
    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
        recipeRepository.deleteById(id);
    }

    private RecipeResponse mapToResponse(Recipe recipe) {
        String productName = productRepository.findById(recipe.getProductId())
                .map(p -> p.getName())
                .orElse("Unknown Product");

        List<RecipeResponse.IngredientResponse> ingredientResponses = recipe.getIngredients().stream()
                .map(ing -> RecipeResponse.IngredientResponse.builder()
                        .ingredientId(ing.getIngredientId())
                        .ingredientName("Ingredient " + ing.getIngredientId())
                        .quantity(ing.getQuantity())
                        .unit(ing.getUnit())
                        .build())
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

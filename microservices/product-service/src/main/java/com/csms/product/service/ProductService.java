package com.csms.product.service;

import com.csms.product.dto.ProductRequest;
import com.csms.product.dto.ProductResponse;
import com.csms.product.entity.Category;
import com.csms.product.entity.Product;
import com.csms.product.exception.ResourceNotFoundException;
import com.csms.product.exception.ValidationException;
import com.csms.product.repository.CategoryRepository;
import com.csms.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final com.csms.product.repository.ProductVariantRepository variantRepository;
    private final com.csms.product.repository.RecipeRepository recipeRepository;
    private final com.csms.product.client.InventoryClient inventoryClient;

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse> getAllProducts(
            Long categoryId, String categoryName, Boolean available, String search, org.springframework.data.domain.Pageable pageable) {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return productRepository.findByFilters(categoryId, categoryName, available, search, pageable)
                .map(p -> mapToResponse(p, ingredientData));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product, fetchIngredientData());
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse> getProductsByCategory(Long categoryId, org.springframework.data.domain.Pageable pageable) {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(p -> mapToResponse(p, ingredientData));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse> getProductsByCategoryName(String categoryName, org.springframework.data.domain.Pageable pageable) {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return productRepository.findByCategoryNameIgnoreCase(categoryName, pageable)
                .map(p -> mapToResponse(p, ingredientData));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse> getAvailableProducts(org.springframework.data.domain.Pageable pageable) {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return productRepository.findByAvailable(true, pageable)
                .map(p -> mapToResponse(p, ingredientData));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProductResponse> searchProducts(String keyword, org.springframework.data.domain.Pageable pageable) {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData = fetchIngredientData();
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(p -> mapToResponse(p, ingredientData));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new ValidationException("Product name already exists");
        }

        Category category = resolveCategory(request);
        boolean available = resolveAvailable(request);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .available(available)
                .imageUrl(request.getImageUrl())
                .build();

        Product savedProduct = productRepository.save(product);

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            java.util.List<com.csms.product.entity.ProductVariant> variants = request.getVariants().stream()
                    .map(v -> com.csms.product.entity.ProductVariant.builder()
                            .size(v.getSize())
                            .temperature(v.getTemperature())
                            .price(v.getPrice())
                            .sku(v.getSku())
                            .product(savedProduct)
                            .build())
                    .collect(Collectors.toList());
            variantRepository.saveAll(variants);
            savedProduct.setVariants(variants);
        }

        // Handle Ingredients (Create/Update Recipe)
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            List<ProductRequest.IngredientRequest> normalizedIngredients = normalizeIngredients(request.getIngredients());
            com.csms.product.entity.Recipe recipe = com.csms.product.entity.Recipe.builder()
                    .productId(savedProduct.getId())
                    .instructions("Standard preparation")
                    .prepTime(5)
                    .build();
            
            com.csms.product.entity.Recipe savedRecipe = recipeRepository.save(recipe);
            
            java.util.List<com.csms.product.entity.RecipeIngredient> recipeIngredients = normalizedIngredients.stream()
                    .map(i -> com.csms.product.entity.RecipeIngredient.builder()
                            .recipe(savedRecipe)
                            .ingredientId(i.getIngredientId())
                            .quantity(i.getQuantity())
                            .unit(i.getUnit())
                            .build())
                    .collect(Collectors.toList());
            
            savedRecipe.getIngredients().addAll(recipeIngredients);
            recipeRepository.save(savedRecipe);
        }

        return mapToResponse(savedProduct, fetchIngredientData());
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new ValidationException("Product name already exists");
        }

        Category category = resolveCategory(request);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setAvailable(resolveAvailable(request));
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct = productRepository.save(product);

        // Update variants - Clear and rebuild for simplicity and consistency
        if (request.getVariants() != null) {
            if (updatedProduct.getVariants() == null) {
                updatedProduct.setVariants(new java.util.ArrayList<>());
            } else {
                updatedProduct.getVariants().clear();
            }
            
            java.util.List<com.csms.product.entity.ProductVariant> variants = request.getVariants().stream()
                    .map(v -> com.csms.product.entity.ProductVariant.builder()
                            .size(v.getSize())
                            .temperature(v.getTemperature())
                            .price(v.getPrice())
                            .sku(v.getSku())
                            .product(updatedProduct)
                            .build())
                    .collect(Collectors.toList());
            
            updatedProduct.getVariants().addAll(variants);
            productRepository.save(updatedProduct);
        }

        // Update Ingredients
        if (request.getIngredients() != null) {
            List<ProductRequest.IngredientRequest> normalizedIngredients = normalizeIngredients(request.getIngredients());
            com.csms.product.entity.Recipe recipe = recipeRepository.findByProductId(id)
                    .orElseGet(() -> com.csms.product.entity.Recipe.builder()
                            .productId(id)
                            .instructions("Standard preparation")
                            .prepTime(5)
                            .build());
            
            if (recipe.getIngredients() == null) {
                recipe.setIngredients(new java.util.ArrayList<>());
            } else {
                recipe.getIngredients().clear();
            }
            
            for (com.csms.product.dto.ProductRequest.IngredientRequest i : normalizedIngredients) {
                if (i.getIngredientId() != null && i.getQuantity() != null) {
                    com.csms.product.entity.RecipeIngredient ri = com.csms.product.entity.RecipeIngredient.builder()
                            .recipe(recipe)
                            .ingredientId(i.getIngredientId())
                            .quantity(i.getQuantity())
                            .unit(i.getUnit())
                            .build();
                    recipe.getIngredients().add(ri);
                }
            }
            recipeRepository.save(recipe);
        }

        return mapToResponse(updatedProduct, fetchIngredientData());
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Resolves category from request - first tries categoryId, then falls back to category name
     */
    private Category resolveCategory(ProductRequest request) {
        if (request.getCategoryId() != null) {
            return categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            // Try to find or create the category by name
            return categoryRepository.findByName(request.getCategory())
                    .orElseGet(() -> categoryRepository.save(Category.builder()
                            .name(request.getCategory())
                            .build()));
        }
        throw new ValidationException("Category is required. Provide either categoryId or category name.");
    }

    /**
     * Resolves available flag from status string or available boolean
     */
    private boolean resolveAvailable(ProductRequest request) {
        if (request.getStatus() != null) {
            return !"UNAVAILABLE".equalsIgnoreCase(request.getStatus());
        }
        return request.getAvailable() != null ? request.getAvailable() : true;
    }

    private List<ProductRequest.IngredientRequest> normalizeIngredients(List<ProductRequest.IngredientRequest> ingredients) {
        LinkedHashMap<Long, ProductRequest.IngredientRequest> merged = new LinkedHashMap<>();

        for (ProductRequest.IngredientRequest item : ingredients) {
            if (item == null || item.getIngredientId() == null) {
                continue;
            }
            if (item.getQuantity() == null || item.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                continue;
            }

            ProductRequest.IngredientRequest existing = merged.get(item.getIngredientId());
            if (existing == null) {
                ProductRequest.IngredientRequest copy = new ProductRequest.IngredientRequest();
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

        return new java.util.ArrayList<>(merged.values());
    }

    private java.util.Map<Long, com.csms.product.dto.IngredientResponse> fetchIngredientData() {
        java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientMap = new java.util.HashMap<>();
        try {
            java.util.List<com.csms.product.dto.IngredientResponse> ingredients = inventoryClient.getAllIngredients();
            if (ingredients != null) {
                log.info("ProductService: Fetched {} ingredients from inventory-service", ingredients.size());
                ingredients.forEach(i -> {
                    if (i.getId() != null) {
                        ingredientMap.put(i.getId(), i);
                    }
                });
            } else {
                log.warn("ProductService: Inventory service returned null list of ingredients");
            }
        } catch (Exception e) {
            log.error("ProductService: Failed to fetch ingredient data: {}", e.getMessage());
        }
        return ingredientMap;
    }

    private ProductResponse mapToResponse(Product product, java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .available(product.getAvailable())
                .imageUrl(product.getImageUrl())
                .status(product.getAvailable() != null && product.getAvailable() ? "Available" : "Unavailable")
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .variants(product.getVariants() == null ? java.util.Collections.emptyList() : 
                    product.getVariants().stream()
                        .map(v -> ProductResponse.VariantResponse.builder()
                            .id(v.getId())
                            .size(v.getSize())
                            .temperature(v.getTemperature())
                            .price(v.getPrice())
                            .sku(v.getSku())
                            .build())
                        .collect(Collectors.toList()))
                .ingredients(fetchIngredientsInternal(product.getId(), ingredientData))
                .build();
    }

    private java.util.List<ProductResponse.IngredientResponse> fetchIngredientsInternal(Long productId, java.util.Map<Long, com.csms.product.dto.IngredientResponse> ingredientData) {
        return recipeRepository.findByProductId(productId)
                .map(recipe -> recipe.getIngredients().stream()
                        .map(ri -> {
                            com.csms.product.dto.IngredientResponse data = ingredientData.get(ri.getIngredientId());
                            String name = data != null ? data.getName() : "Ingredient #" + ri.getIngredientId();
                            java.math.BigDecimal stock = data != null ? data.getCurrentStock() : java.math.BigDecimal.ZERO;
                            
                            return ProductResponse.IngredientResponse.builder()
                                    .ingredientId(ri.getIngredientId())
                                    .name(name)
                                    .quantity(ri.getQuantity())
                                    .unit(ri.getUnit())
                                    .currentStock(stock)
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .orElse(java.util.Collections.emptyList());
    }
}

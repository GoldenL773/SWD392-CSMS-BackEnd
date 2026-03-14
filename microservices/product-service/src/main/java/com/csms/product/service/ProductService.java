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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final com.csms.product.repository.ProductVariantRepository variantRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findByAvailable(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

        return mapToResponse(savedProduct);
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

        Product updatedProduct = productRepository.save(product);

        // Update variants
        if (request.getVariants() != null) {
            if (product.getVariants() == null) {
                product.setVariants(new java.util.ArrayList<>());
            } else {
                product.getVariants().clear();
            }
            
            java.util.List<com.csms.product.entity.ProductVariant> variants = request.getVariants().stream()
                    .map(v -> com.csms.product.entity.ProductVariant.builder()
                            .size(v.getSize())
                            .temperature(v.getTemperature())
                            .price(v.getPrice())
                            .sku(v.getSku())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            
            product.getVariants().addAll(variants);
        }

        return mapToResponse(product);
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

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .available(product.getAvailable())
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
                .build();
    }
}

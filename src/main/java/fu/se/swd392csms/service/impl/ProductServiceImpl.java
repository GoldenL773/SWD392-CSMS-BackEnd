package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.ProductRequest;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.ProductResponse;
import fu.se.swd392csms.entity.Ingredient;
import fu.se.swd392csms.entity.Product;
import fu.se.swd392csms.entity.ProductIngredient;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.IngredientRepository;
import fu.se.swd392csms.repository.ProductIngredientRepository;
import fu.se.swd392csms.repository.ProductRepository;
import fu.se.swd392csms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Service Implementation
 * Implements product management business logic
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductIngredientRepository productIngredientRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToResponse);
    }
    
    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponse> getProductsByStatus(String status) {
        return productRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return convertToResponse(product);
    }
    
    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        // Check if product name already exists
        if (productRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Product with name '" + request.getName() + "' already exists");
        }
        
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .price(request.getPrice())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();
        
        Product savedProduct = productRepository.save(product);
        
        // Save product ingredients if provided
        if (request.getProductIngredients() != null && !request.getProductIngredients().isEmpty()) {
            for (ProductRequest.ProductIngredientRequest piRequest : request.getProductIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(piRequest.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", piRequest.getIngredientId()));
                
                ProductIngredient productIngredient = ProductIngredient.builder()
                        .product(savedProduct)
                        .ingredient(ingredient)
                        .quantityRequired(BigDecimal.valueOf(piRequest.getQuantityRequired()))
                        .build();
                
                productIngredientRepository.save(productIngredient);
            }
        }
        
        return convertToResponse(savedProduct);
    }
    
    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Check if new name conflicts with existing product
        productRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("Product with name '" + request.getName() + "' already exists");
            }
        });
        
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus());
        product.setDescription(request.getDescription());
        
        Product updatedProduct = productRepository.save(product);
        
        // Update product ingredients
        if (request.getProductIngredients() != null) {
            // Delete existing product ingredients
            productIngredientRepository.deleteByProductId(id);
            
            // Add new product ingredients
            for (ProductRequest.ProductIngredientRequest piRequest : request.getProductIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(piRequest.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", piRequest.getIngredientId()));
                
                ProductIngredient productIngredient = ProductIngredient.builder()
                        .product(updatedProduct)
                        .ingredient(ingredient)
                        .quantityRequired(BigDecimal.valueOf(piRequest.getQuantityRequired()))
                        .build();
                
                productIngredientRepository.save(productIngredient);
            }
        }
        
        return convertToResponse(updatedProduct);
    }
    
    @Override
    @Transactional
    public MessageResponse deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        productRepository.delete(product);
        return new MessageResponse("Product deleted successfully");
    }
    
    @Override
    public List<ProductResponse> searchProducts(String name) {
        return productRepository.searchByName(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<ProductResponse> searchAndFilterProducts(String category, String status, String searchTerm, Pageable pageable) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by category
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category")), 
                    category.toLowerCase()
                ));
            }
            
            // Filter by status
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("status")), 
                    status.toLowerCase()
                ));
            }
            
            // Search by name
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + searchTerm.toLowerCase() + "%"
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return productRepository.findAll(spec, pageable)
                .map(this::convertToResponse);
    }
    
    /**
     * Convert Product entity to ProductResponse DTO
     */
    private ProductResponse convertToResponse(Product product) {
        // Fetch product ingredients
        List<ProductIngredient> productIngredients = productIngredientRepository.findByProductIdWithIngredient(product.getId());
        
        // Compute availability status based on ingredients
        boolean hasOutOfStock = false;
        boolean hasLowStock = false;
        
        List<ProductResponse.ProductIngredientInfo> ingredients = productIngredients.stream()
                .map(pi -> {
                    Ingredient ingredient = pi.getIngredient();
                    boolean isLowStock = ingredient.getQuantity().compareTo(ingredient.getMinimumStock()) < 0;
                    boolean isOutOfStock = ingredient.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0;
                    
                    return ProductResponse.ProductIngredientInfo.builder()
                            .ingredientId(ingredient.getId())
                            .ingredientName(ingredient.getName())
                            .requiredQuantity(pi.getQuantityRequired())
                            .unit(ingredient.getUnit())
                            .currentQuantity(ingredient.getQuantity())
                            .minimumStock(ingredient.getMinimumStock())
                            .isLowStock(isLowStock)
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());
        
        // Check if any ingredient is out of stock or low stock
        for (ProductResponse.ProductIngredientInfo info : ingredients) {
            if (info.getCurrentQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                hasOutOfStock = true;
                break;
            }
            if (info.getIsLowStock()) {
                hasLowStock = true;
            }
        }
        
        // Determine availability status
        String availabilityStatus = "IN_STOCK";
        boolean isAvailable = true;
        
        if (hasOutOfStock) {
            availabilityStatus = "OUT_OF_STOCK";
            isAvailable = false;
        } else if (hasLowStock) {
            availabilityStatus = "LOW_STOCK";
        }
        
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .status(product.getStatus())
                .description(product.getDescription())
                .productIngredients(ingredients)
                .availabilityStatus(availabilityStatus)
                .isAvailable(isAvailable)
                .isLowStock(hasLowStock)
                .build();
    }
}

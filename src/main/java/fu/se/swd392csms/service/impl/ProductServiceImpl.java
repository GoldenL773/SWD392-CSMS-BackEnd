package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.ProductRequest;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.ProductResponse;
import fu.se.swd392csms.entity.Product;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.ProductRepository;
import fu.se.swd392csms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // If search term is provided, use search
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            return productRepository.searchByName(searchTerm).stream()
                    .filter(p -> (category == null || category.isEmpty() || p.getCategory().equalsIgnoreCase(category)))
                    .filter(p -> (status == null || status.isEmpty() || p.getStatus().equalsIgnoreCase(status)))
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(this::convertToResponse)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        }
        
        // Filter by category and status
        if (category != null && !category.isEmpty() && status != null && !status.isEmpty()) {
            return productRepository.findByCategoryAndStatus(category, status).stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(this::convertToResponse)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        }
        
        // Filter by category only
        if (category != null && !category.isEmpty()) {
            return productRepository.findByCategory(category).stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(this::convertToResponse)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        }
        
        // Filter by status only
        if (status != null && !status.isEmpty()) {
            return productRepository.findByStatus(status).stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(this::convertToResponse)
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        }
        
        // No filters, return all with pagination
        return getAllProducts(pageable);
    }
    
    /**
     * Convert Product entity to ProductResponse DTO
     */
    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .status(product.getStatus())
                .description(product.getDescription())
                .build();
    }
}

package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.request.ProductRequest;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.ProductResponse;
import fu.se.swd392csms.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller
 * Handles product management endpoints
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * Get all products with pagination and filtering
     */
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products with pagination and filtering")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        // Create pageable object with sorting
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Use search and filter method
        Page<ProductResponse> products = productService.searchAndFilterProducts(
                category, status, search, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve product details by ID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Create new product
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Create product", description = "Create a new product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    /**
     * Update product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @Operation(summary = "Update product", description = "Update existing product")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Delete product
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete product", description = "Delete a product")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        MessageResponse response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }
}

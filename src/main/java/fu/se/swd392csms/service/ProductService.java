package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.ProductRequest;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Product Service Interface
 * Handles product management operations
 */
public interface ProductService {
    
    /**
     * Get all products with pagination
     * @param pageable Pagination parameters
     * @return Page of products
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);
    
    /**
     * Get all products
     * @return List of all products
     */
    List<ProductResponse> getAllProducts();
    
    /**
     * Get products by category
     * @param category Product category
     * @return List of products in the category
     */
    List<ProductResponse> getProductsByCategory(String category);
    
    /**
     * Get products by status
     * @param status Product status
     * @return List of products with the status
     */
    List<ProductResponse> getProductsByStatus(String status);
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return Product details
     */
    ProductResponse getProductById(Long id);
    
    /**
     * Create new product
     * @param request Product details
     * @return Created product
     */
    ProductResponse createProduct(ProductRequest request);
    
    /**
     * Update existing product
     * @param id Product ID
     * @param request Updated product details
     * @return Updated product
     */
    ProductResponse updateProduct(Long id, ProductRequest request);
    
    /**
     * Delete product
     * @param id Product ID
     * @return Success message
     */
    MessageResponse deleteProduct(Long id);
    
    /**
     * Search products by name
     * @param name Product name
     * @return List of matching products
     */
    List<ProductResponse> searchProducts(String name);
    
    /**
     * Search and filter products with pagination
     * @param category Product category (optional)
     * @param status Product status (optional)
     * @param searchTerm Search term for name (optional)
     * @param pageable Pagination parameters
     * @return Page of filtered products
     */
    Page<ProductResponse> searchAndFilterProducts(String category, String status, String searchTerm, Pageable pageable);
}

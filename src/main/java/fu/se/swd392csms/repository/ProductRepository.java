package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity
 * Provides CRUD operations and custom queries for products
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find all products by category
     * @param category Product category (Coffee, Tea, Cake, etc.)
     * @return List of products in the category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find all products by status
     * @param status Product status (Available, Unavailable)
     * @return List of products with the given status
     */
    List<Product> findByStatus(String status);
    
    /**
     * Find product by name
     * @param name Product name
     * @return Optional containing the product if found
     */
    Optional<Product> findByName(String name);
    
    /**
     * Find all available products
     * @return List of available products
     */
    @Query("SELECT p FROM Product p WHERE p.status = 'Available' ORDER BY p.category, p.name")
    List<Product> findAllAvailableProducts();
    
    /**
     * Find products by category and status
     * @param category Product category
     * @param status Product status
     * @return List of matching products
     */
    List<Product> findByCategoryAndStatus(String category, String status);
    
    /**
     * Search products by name (case-insensitive)
     * @param name Name to search
     * @return List of matching products
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByName(String name);
    
    /**
     * Get all distinct categories
     * @return List of unique categories
     */
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
}

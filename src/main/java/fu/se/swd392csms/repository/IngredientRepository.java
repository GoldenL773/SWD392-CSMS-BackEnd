package fu.se.swd392csms.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fu.se.swd392csms.entity.Ingredient;

/**
 * Repository interface for Ingredient entity
 * Provides CRUD operations and custom queries for ingredients
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    /**
     * Find ingredient by name
     * @param name Ingredient name
     * @return Optional containing the ingredient if found
     */
    Optional<Ingredient> findByName(String name);

    /**
     * Check if ingredient exists by name
     * @param name Ingredient name
     * @return true if ingredient exists with the given name
     */
    boolean existsByName(String name);

    /**
     * Find all ingredients by supplier
     * @param supplier Supplier name
     * @return List of ingredients from the supplier
     */
    List<Ingredient> findBySupplier(String supplier);

    /**
     * Find ingredients by name containing (case-insensitive) with pagination
     * @param name Name to search
     * @param pageable Pagination information
     * @return Page of matching ingredients
     */
    Page<Ingredient> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find ingredients with low stock (quantity below threshold)
     * @param threshold Minimum quantity threshold
     * @return List of ingredients with low stock
     */
    @Query("SELECT i FROM Ingredient i WHERE i.quantity < :threshold ORDER BY i.quantity")
    List<Ingredient> findLowStockIngredients(@Param("threshold") BigDecimal threshold);

    /**
     * Find ingredients with quantity less than or equal to specified amount
     * @param quantity Quantity threshold
     * @return List of ingredients
     */
    List<Ingredient> findByQuantityLessThanEqual(BigDecimal quantity);

    /**
     * Search ingredients by name (case-insensitive)
     * @param name Name to search
     * @return List of matching ingredients
     */
    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Ingredient> searchByName(String name);

    /**
     * Get all distinct suppliers
     * @return List of unique suppliers
     */
    @Query("SELECT DISTINCT i.supplier FROM Ingredient i WHERE i.supplier IS NOT NULL ORDER BY i.supplier")
    List<String> findAllSuppliers();
}

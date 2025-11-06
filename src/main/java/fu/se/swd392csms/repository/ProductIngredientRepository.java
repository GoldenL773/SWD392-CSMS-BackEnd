package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ProductIngredient entity
 * Provides CRUD operations and custom queries for product-ingredient relationships
 */
@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {
    
    /**
     * Find all ingredients for a product
     * @param productId Product ID
     * @return List of product-ingredient mappings
     */
    List<ProductIngredient> findByProductId(Long productId);
    
    /**
     * Find all products using an ingredient
     * @param ingredientId Ingredient ID
     * @return List of product-ingredient mappings
     */
    List<ProductIngredient> findByIngredientId(Long ingredientId);
    
    /**
     * Find product-ingredient mapping by product and ingredient
     * @param productId Product ID
     * @param ingredientId Ingredient ID
     * @return List of matching mappings (should be 0 or 1)
     */
    List<ProductIngredient> findByProductIdAndIngredientId(Long productId, Long ingredientId);
    
    /**
     * Delete all mappings for a product
     * @param productId Product ID
     */
    void deleteByProductId(Long productId);
    
    /**
     * Delete all mappings for an ingredient
     * @param ingredientId Ingredient ID
     */
    void deleteByIngredientId(Long ingredientId);
    
    /**
     * Get all ingredients for a product with details
     * @param productId Product ID
     * @return List of product-ingredient mappings with ingredient details
     */
    @Query("SELECT pi FROM ProductIngredient pi JOIN FETCH pi.ingredient WHERE pi.product.id = :productId")
    List<ProductIngredient> findByProductIdWithIngredient(Long productId);
}

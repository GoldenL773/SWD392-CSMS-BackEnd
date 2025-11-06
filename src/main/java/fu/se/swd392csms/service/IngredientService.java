package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.IngredientRequest;
import fu.se.swd392csms.dto.request.IngredientTransactionRequest;
import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Ingredient Service Interface
 * Handles business logic for ingredient/inventory management
 */
public interface IngredientService {
    
    /**
     * Create a new ingredient
     * @param request Ingredient creation request
     * @return Created ingredient response
     */
    IngredientResponse createIngredient(IngredientRequest request);
    
    /**
     * Get ingredient by ID
     * @param id Ingredient ID
     * @return Ingredient response
     */
    IngredientResponse getIngredientById(Long id);
    
    /**
     * Get all ingredients with optional search and pagination
     * @param search Optional search term
     * @param pageable Pagination parameters
     * @return Page of ingredients
     */
    Page<IngredientResponse> getAllIngredients(String search, Pageable pageable);
    
    /**
     * Update ingredient
     * @param id Ingredient ID
     * @param request Ingredient update request
     * @return Updated ingredient response
     */
    IngredientResponse updateIngredient(Long id, IngredientRequest request);
    
    /**
     * Delete ingredient
     * @param id Ingredient ID
     * @return Success message
     */
    MessageResponse deleteIngredient(Long id);
    
    /**
     * Record ingredient transaction (import/export)
     * @param request Transaction request
     * @return Created transaction response
     */
    IngredientTransactionResponse recordTransaction(IngredientTransactionRequest request);
    
    /**
     * Get ingredient transactions
     * @param ingredientId Optional ingredient ID filter
     * @param type Optional transaction type filter (IMPORT/EXPORT)
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    Page<IngredientTransactionResponse> getTransactions(Long ingredientId, String type, Pageable pageable);
    
    /**
     * Get low stock ingredients
     * @return List of ingredients below minimum stock
     */
    List<IngredientResponse> getLowStockIngredients();
}

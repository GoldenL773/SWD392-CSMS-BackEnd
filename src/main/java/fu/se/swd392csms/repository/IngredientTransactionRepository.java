package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.IngredientTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for IngredientTransaction entity
 * Provides CRUD operations and custom queries for ingredient transactions
 */
@Repository
public interface IngredientTransactionRepository extends JpaRepository<IngredientTransaction, Long> {
    
    /**
     * Find all transactions for an ingredient
     * @param ingredientId Ingredient ID
     * @return List of transactions ordered by date descending
     */
    @Query("SELECT t FROM IngredientTransaction t WHERE t.ingredient.id = :ingredientId ORDER BY t.transactionDate DESC")
    List<IngredientTransaction> findByIngredientId(Long ingredientId);
    
    /**
     * Find all transactions by type
     * @param type Transaction type (Import, Export)
     * @return List of transactions
     */
    List<IngredientTransaction> findByType(String type);
    
    /**
     * Find transactions by employee
     * @param employeeId Employee ID
     * @return List of transactions
     */
    List<IngredientTransaction> findByEmployeeId(Long employeeId);
    
    /**
     * Find transactions within date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     */
    @Query("SELECT t FROM IngredientTransaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<IngredientTransaction> findByTransactionDateBetween(@Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find transactions by ingredient and type
     * @param ingredientId Ingredient ID
     * @param type Transaction type
     * @return List of transactions
     */
    @Query("SELECT t FROM IngredientTransaction t WHERE t.ingredient.id = :ingredientId AND t.type = :type ORDER BY t.transactionDate DESC")
    List<IngredientTransaction> findByIngredientIdAndType(@Param("ingredientId") Long ingredientId,
                                                          @Param("type") String type);
    
    /**
     * Find transactions by type and date range
     * @param type Transaction type
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     */
    @Query("SELECT t FROM IngredientTransaction t WHERE t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<IngredientTransaction> findByTypeAndDateRange(@Param("type") String type,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get all transactions ordered by date descending
     * @return List of all transactions
     */
    @Query("SELECT t FROM IngredientTransaction t ORDER BY t.transactionDate DESC")
    List<IngredientTransaction> findAllOrderByDateDesc();
}

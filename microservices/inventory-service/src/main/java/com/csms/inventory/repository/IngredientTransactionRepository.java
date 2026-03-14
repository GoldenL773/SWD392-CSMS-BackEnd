package com.csms.inventory.repository;

import com.csms.inventory.entity.IngredientTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientTransactionRepository extends JpaRepository<IngredientTransaction, Long> {
    List<IngredientTransaction> findByIngredientId(Long ingredientId);
    List<IngredientTransaction> findByTransactionType(String transactionType);
}

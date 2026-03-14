package com.csms.inventory.repository;

import com.csms.inventory.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    boolean existsByName(String name);
    List<Ingredient> findByCurrentStockLessThanEqual(BigDecimal threshold);
}

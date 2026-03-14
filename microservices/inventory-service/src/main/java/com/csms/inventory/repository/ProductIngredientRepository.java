package com.csms.inventory.repository;

import com.csms.inventory.entity.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Long> {
    List<ProductIngredient> findByProductId(Long productId);
    List<ProductIngredient> findByIngredientId(Long ingredientId);
    Optional<ProductIngredient> findByProductIdAndIngredientId(Long productId, Long ingredientId);
    void deleteByProductId(Long productId);
}

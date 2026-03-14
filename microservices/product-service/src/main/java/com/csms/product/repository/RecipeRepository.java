package com.csms.product.repository;

import com.csms.product.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    java.util.Optional<Recipe> findByProductId(Long productId);
}

package com.csms.product.repository;

import com.csms.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    boolean existsByName(String name);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByAvailable(Boolean available, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByCategoryNameIgnoreCase(String categoryName, Pageable pageable);
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:categoryName IS NULL OR LOWER(p.category.name) = LOWER(:categoryName)) AND " +
            "(:available IS NULL OR p.available = :available) AND " +
            "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findByFilters(
            @org.springframework.data.repository.query.Param("categoryId") Long categoryId,
            @org.springframework.data.repository.query.Param("categoryName") String categoryName,
            @org.springframework.data.repository.query.Param("available") Boolean available,
            @org.springframework.data.repository.query.Param("search") String search,
            Pageable pageable);
}

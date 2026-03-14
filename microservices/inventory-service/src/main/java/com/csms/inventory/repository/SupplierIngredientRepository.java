package com.csms.inventory.repository;

import com.csms.inventory.entity.SupplierIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierIngredientRepository extends JpaRepository<SupplierIngredient, Long> {
    List<SupplierIngredient> findBySupplierId(Long supplierId);
    void deleteBySupplierId(Long supplierId);
}

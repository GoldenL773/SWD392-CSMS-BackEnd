package com.csms.inventory.service;

import com.csms.inventory.dto.SupplierRequest;
import com.csms.inventory.dto.SupplierResponse;
import com.csms.inventory.entity.Ingredient;
import com.csms.inventory.entity.Supplier;
import com.csms.inventory.entity.SupplierIngredient;
import com.csms.inventory.exception.ResourceNotFoundException;
import com.csms.inventory.repository.IngredientRepository;
import com.csms.inventory.repository.SupplierIngredientRepository;
import com.csms.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierIngredientRepository supplierIngredientRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .build();

        supplier = supplierRepository.save(supplier);

        saveSupplierIngredients(supplier, request.getProvidedIngredients());

        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        if (request.getStatus() != null) {
            supplier.setStatus(request.getStatus());
        }

        supplier = supplierRepository.save(supplier);

        // Update ingredients: delete old and insert new
        supplierIngredientRepository.deleteBySupplierId(supplier.getId());
        saveSupplierIngredients(supplier, request.getProvidedIngredients());

        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        supplierIngredientRepository.deleteBySupplierId(id);
        supplierRepository.delete(supplier);
    }

    private void saveSupplierIngredients(Supplier supplier, List<Long> ingredientIds) {
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            List<SupplierIngredient> supplierIngredients = ingredientIds.stream().map(ingredientId -> {
                Ingredient ingredient = ingredientRepository.findById(ingredientId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + ingredientId));
                return SupplierIngredient.builder()
                        .supplier(supplier)
                        .ingredient(ingredient)
                        .build();
            }).collect(Collectors.toList());
            supplierIngredientRepository.saveAll(supplierIngredients);
        }
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        List<Long> ingredientIds = supplierIngredientRepository.findBySupplierId(supplier.getId())
                .stream()
                .map(si -> si.getIngredient().getId())
                .collect(Collectors.toList());

        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .status(supplier.getStatus())
                .providedIngredients(ingredientIds)
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}

package com.csms.inventory.service;

import com.csms.inventory.dto.SupplierRequest;
import com.csms.inventory.dto.SupplierResponse;

import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getAllSuppliers();
    SupplierResponse getSupplierById(Long id);
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    void deleteSupplier(Long id);
}

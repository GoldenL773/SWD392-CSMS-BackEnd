package com.csms.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String status;
    private List<Long> providedIngredients;
}

package com.csms.product.service;

import com.csms.product.dto.ComboRequest;
import com.csms.product.dto.ComboResponse;
import com.csms.product.entity.Combo;
import com.csms.product.entity.ComboItem;
import com.csms.product.entity.Product;
import com.csms.product.exception.ResourceNotFoundException;
import com.csms.product.repository.ComboRepository;
import com.csms.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComboService {

    private final ComboRepository comboRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ComboResponse> getAllCombos() {
        return comboRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComboResponse getComboById(Long id) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo not found with id: " + id));
        return mapToResponse(combo);
    }

    @Transactional
    public ComboResponse createCombo(ComboRequest request) {
        Combo combo = Combo.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(request.getStatus() != null ? request.getStatus() : "AVAILABLE")
                .imageUrl(request.getImageUrl())
                .items(new ArrayList<>())
                .build();

        combo = comboRepository.save(combo);

        if (request.getItems() != null) {
            for (ComboRequest.ComboItemRequest itemReq : request.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));
                ComboItem item = ComboItem.builder()
                        .combo(combo)
                        .product(product)
                        .quantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                        .build();
                combo.getItems().add(item);
            }
            combo = comboRepository.save(combo);
        }

        return mapToResponse(combo);
    }

    @Transactional
    public ComboResponse updateCombo(Long id, ComboRequest request) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo not found with id: " + id));

        combo.setName(request.getName());
        combo.setDescription(request.getDescription());
        combo.setPrice(request.getPrice());
        if (request.getStatus() != null) {
            combo.setStatus(request.getStatus());
        }
        combo.setImageUrl(request.getImageUrl());

        combo.getItems().clear();

        if (request.getItems() != null) {
            for (ComboRequest.ComboItemRequest itemReq : request.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));
                ComboItem item = ComboItem.builder()
                        .combo(combo)
                        .product(product)
                        .quantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                        .build();
                combo.getItems().add(item);
            }
        }

        return mapToResponse(comboRepository.save(combo));
    }

    @Transactional
    public void deleteCombo(Long id) {
        Combo combo = comboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combo not found with id: " + id));
        comboRepository.delete(combo);
    }

    private ComboResponse mapToResponse(Combo combo) {
        List<ComboResponse.ComboItemResponse> itemResponses = combo.getItems().stream()
                .map(item -> ComboResponse.ComboItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return ComboResponse.builder()
                .id(combo.getId())
                .name(combo.getName())
                .description(combo.getDescription())
                .price(combo.getPrice())
                .status(combo.getStatus())
                .imageUrl(combo.getImageUrl())
                .items(itemResponses)
                .createdAt(combo.getCreatedAt())
                .updatedAt(combo.getUpdatedAt())
                .build();
    }
}

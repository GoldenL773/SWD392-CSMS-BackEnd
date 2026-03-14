package com.csms.product.service;

import com.csms.product.dto.PromotionRequest;
import com.csms.product.dto.PromotionResponse;
import com.csms.product.entity.Promotion;
import com.csms.product.exception.ResourceNotFoundException;
import com.csms.product.repository.ComboRepository;
import com.csms.product.repository.ProductRepository;
import com.csms.product.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final ComboRepository comboRepository;

    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        return mapToResponse(promotion);
    }

    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        if (promotionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Promotion name already exists: " + request.getName());
        }

        Promotion promotion = Promotion.builder()
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .applyTo(request.getApplyTo())
                .targetId(request.getTargetId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        return mapToResponse(promotionRepository.save(promotion));
    }

    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));

        // Check name uniqueness if changed
        if (!promotion.getName().equals(request.getName()) && promotionRepository.existsByName(request.getName())) {
            throw new RuntimeException("Promotion name already exists: " + request.getName());
        }

        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setApplyTo(request.getApplyTo());
        promotion.setTargetId(request.getTargetId());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        if (request.getStatus() != null) {
            promotion.setStatus(request.getStatus());
        }

        return mapToResponse(promotionRepository.save(promotion));
    }

    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        promotionRepository.delete(promotion);
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        String targetName = "Unknown";
        if ("PRODUCT".equalsIgnoreCase(promotion.getApplyTo())) {
            targetName = productRepository.findById(promotion.getTargetId())
                    .map(p -> p.getName())
                    .orElse("Unknown Product");
        } else if ("COMBO".equalsIgnoreCase(promotion.getApplyTo())) {
            targetName = comboRepository.findById(promotion.getTargetId())
                    .map(c -> c.getName())
                    .orElse("Unknown Combo");
        }

        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .applyTo(promotion.getApplyTo())
                .targetId(promotion.getTargetId())
                .targetName(targetName)
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .status(promotion.getStatus())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}

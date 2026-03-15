package com.csms.order.saga;

import com.csms.order.client.InventoryClient;
import com.csms.order.client.ProductClient;
import com.csms.order.dto.InventoryTransactionRequest;
import com.csms.order.dto.ProductIngredientResponse;
import com.csms.order.dto.ProductResponse;
import com.csms.order.dto.ComboResponse;
import com.csms.order.dto.PromotionResponse;
import com.csms.order.entity.Order;
import com.csms.order.entity.OrderItem;
import com.csms.order.exception.SagaException;
import com.csms.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(OrderSagaOrchestrator.class);
    
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    public void processOrder(Order order) {
        log.info("Starting saga for order: {}", order.getId());
        
        List<InventoryTransactionRequest> successfulTransactions = new ArrayList<>();
        
        try {
            // Step 1: Validate Products & Calculate Subtotals
            order.setStatus("VALIDATING");
            orderRepository.save(order);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (OrderItem item : order.getItems()) {
                if (item.getComboId() != null) {
                    ComboResponse combo = productClient.getComboById(item.getComboId());
                    item.setProductName(combo.getName());
                    item.setUnitPrice(combo.getPrice());
                    
                    BigDecimal subtotal = combo.getPrice().multiply(new BigDecimal(item.getQuantity()));
                    item.setSubtotal(subtotal);
                    totalAmount = totalAmount.add(subtotal);
                    continue;
                }

                ProductResponse product = productClient.getProductById(item.getProductId());
                if (!product.getAvailable()) {
                    throw new SagaException("Product " + product.getName() + " is not available");
                }
                
                item.setProductName(product.getName());
                
                BigDecimal unitPrice = product.getPrice();
                if (item.getVariantId() != null && product.getVariants() != null) {
                    ProductResponse.VariantResponse variant = product.getVariants().stream()
                            .filter(v -> v.getId().equals(item.getVariantId()))
                            .findFirst()
                            .orElseThrow(() -> new SagaException("Variant " + item.getVariantId() + " not found for product " + product.getName()));
                    unitPrice = variant.getPrice();
                    item.setProductName(product.getName() + " (" + variant.getSize() + (variant.getTemperature() != null ? ", " + variant.getTemperature() : "") + ")");
                }
                
                item.setUnitPrice(unitPrice);
                
                BigDecimal subtotal = unitPrice.multiply(new BigDecimal(item.getQuantity()));
                item.setSubtotal(subtotal);
                
                totalAmount = totalAmount.add(subtotal);
            }
            
            // Step 1.5: Apply Promotion (Target-aware)
            BigDecimal totalDiscount = BigDecimal.ZERO;
            if (order.getPromotionId() != null) {
                PromotionResponse promotion = productClient.getPromotionById(order.getPromotionId());
                if (promotion == null || promotion.getApplyTo() == null) {
                    log.warn("Promotion {} not found or incomplete. Skipping.", order.getPromotionId());
                } else {
                    log.info("Applying promotion: {} (ApplyTo: {}, TargetID: {})", promotion.getName(), promotion.getApplyTo(), promotion.getTargetId());
                    
                    String applyTo = promotion.getApplyTo().toUpperCase();
                    
                    if ("ALL".equals(applyTo) || "ORDER".equals(applyTo)) {
                        if ("PERCENTAGE".equals(promotion.getDiscountType())) {
                            totalDiscount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
                        } else {
                            totalDiscount = promotion.getDiscountValue();
                        }
                    } else {
                        for (OrderItem item : order.getItems()) {
                            boolean isMatch = false;
                            if ("PRODUCT".equals(applyTo) && item.getProductId() != null && item.getProductId().equals(promotion.getTargetId())) {
                                isMatch = true;
                            } else if ("COMBO".equals(applyTo) && item.getComboId() != null && item.getComboId().equals(promotion.getTargetId())) {
                                isMatch = true;
                            }

                            if (isMatch) {
                                BigDecimal itemDiscount = BigDecimal.ZERO;
                                if ("PERCENTAGE".equals(promotion.getDiscountType())) {
                                    itemDiscount = item.getSubtotal().multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
                                } else {
                                    itemDiscount = promotion.getDiscountValue();
                                }
                                totalDiscount = totalDiscount.add(itemDiscount);
                                log.info("Matching item found: {}. Applied discount: {}", item.getProductName(), itemDiscount);
                            }
                        }
                    }
                }
                
                // Cap discount at total amount
                if (totalDiscount.compareTo(totalAmount) > 0) {
                    totalDiscount = totalAmount;
                }
            }
            order.setDiscountAmount(totalDiscount);
            order.setTotalAmount(totalAmount.subtract(totalDiscount));
            
            // Step 2: Reserve Inventory (Deduct Ingredients)
            for (OrderItem item : order.getItems()) {
                if (item.getComboId() != null) {
                    ComboResponse combo = productClient.getComboById(item.getComboId());
                    for (ComboResponse.ComboItemResponse comboItem : combo.getItems()) {
                        deductIngredients(order, comboItem.getProductId(), comboItem.getQuantity() * item.getQuantity(), item.getProductName(), successfulTransactions);
                    }
                } else {
                    deductIngredients(order, item.getProductId(), item.getQuantity(), item.getProductName(), successfulTransactions);
                }
            }
            
            // Step 3: Confirm Order - Set status to PROCESSING
            order.setStatus("PROCESSING");
            orderRepository.save(order);
            log.info("Saga completed successfully for order: {}", order.getId());
            
        } catch (Exception e) {
            log.error("Saga failed for order: {}. Initiating compensation.", order.getId(), e);
            compensate(order, successfulTransactions);
            throw new SagaException("Failed to process order: " + e.getMessage());
        }
    }

    private void deductIngredients(Order order, Long productId, int quantity, String displayName, List<InventoryTransactionRequest> successfulTransactions) {
        List<ProductIngredientResponse> ingredients = inventoryClient.getIngredientsByProductId(productId);
        
        for (ProductIngredientResponse ingredient : ingredients) {
            BigDecimal totalQtyToDeduct = ingredient.getQuantity().multiply(new BigDecimal(quantity));
            
            InventoryTransactionRequest request = InventoryTransactionRequest.builder()
                    .ingredientId(ingredient.getIngredientId())
                    .transactionType("USAGE")
                    .quantity(totalQtyToDeduct)
                    .transactionDate(LocalDateTime.now())
                    .note("Order " + order.getId() + " - " + displayName)
                    .build();
            
            inventoryClient.recordTransaction(request);
            successfulTransactions.add(request);
        }
    }

    private void compensate(Order order, List<InventoryTransactionRequest> successfulTransactions) {
        log.info("Compensating for order: {}", order.getId());
        
        // Revert inventory
        for (InventoryTransactionRequest txn : successfulTransactions) {
            try {
                // To revert an EXPORT/USAGE, we IMPORT the same amount back
                InventoryTransactionRequest revertRequest = InventoryTransactionRequest.builder()
                        .ingredientId(txn.getIngredientId())
                        .transactionType("IMPORT")
                        .quantity(txn.getQuantity())
                        .transactionDate(LocalDateTime.now())
                        .note("Revert for failed Order " + order.getId())
                        .build();
                
                inventoryClient.recordTransaction(revertRequest);
            } catch (Exception e) {
                // If compensation fails, we have an inconsistent state (requires manual intervention or async retry)
                log.error("CRITICAL: Failed to revert inventory for ingredient {}", txn.getIngredientId(), e);
            }
        }
        
        // Cancel order
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
    
    public void cancelOrder(Order order) {
        if (!"CONFIRMED".equals(order.getStatus()) && !"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }
        
        log.info("Cancelling order: {}", order.getId());
        
        if ("CONFIRMED".equals(order.getStatus())) {
            // Need to refund inventory
            for (OrderItem item : order.getItems()) {
                if (item.getComboId() != null) {
                    ComboResponse combo = productClient.getComboById(item.getComboId());
                    for (ComboResponse.ComboItemResponse comboItem : combo.getItems()) {
                        refundIngredients(order, comboItem.getProductId(), comboItem.getQuantity() * item.getQuantity(), item.getProductName());
                    }
                } else if (item.getProductId() != null) {
                    refundIngredients(order, item.getProductId(), item.getQuantity(), item.getProductName());
                }
            }
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    private void refundIngredients(Order order, Long productId, int quantity, String displayName) {
        List<ProductIngredientResponse> ingredients = inventoryClient.getIngredientsByProductId(productId);
        
        for (ProductIngredientResponse ingredient : ingredients) {
            BigDecimal totalQtyToRefund = ingredient.getQuantity().multiply(new BigDecimal(quantity));
            
            InventoryTransactionRequest revertRequest = InventoryTransactionRequest.builder()
                    .ingredientId(ingredient.getIngredientId())
                    .transactionType("IMPORT") // Refund back to stock
                    .quantity(totalQtyToRefund)
                    .transactionDate(LocalDateTime.now())
                    .note("Refund for cancelled Order " + order.getId() + " - " + displayName)
                    .build();
            
            inventoryClient.recordTransaction(revertRequest);
        }
    }
}

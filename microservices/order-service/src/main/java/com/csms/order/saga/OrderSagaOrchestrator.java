package com.csms.order.saga;

import com.csms.order.client.InventoryClient;
import com.csms.order.client.ProductClient;
import com.csms.order.dto.InventoryTransactionRequest;
import com.csms.order.dto.ProductIngredientResponse;
import com.csms.order.dto.ProductResponse;
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
                ProductResponse product = productClient.getProductById(item.getProductId());
                if (!product.getAvailable()) {
                    throw new SagaException("Product " + product.getName() + " is not available");
                }
                
                item.setProductName(product.getName());
                item.setUnitPrice(product.getPrice());
                
                BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
                item.setSubtotal(subtotal);
                
                totalAmount = totalAmount.add(subtotal);
            }
            
            order.setTotalAmount(totalAmount);
            
            // Step 2: Reserve Inventory (Deduct Ingredients)
            for (OrderItem item : order.getItems()) {
                List<ProductIngredientResponse> ingredients = inventoryClient.getIngredientsByProductId(item.getProductId());
                
                for (ProductIngredientResponse ingredient : ingredients) {
                    BigDecimal totalQtyToDeduct = ingredient.getQuantity().multiply(new BigDecimal(item.getQuantity()));
                    
                    InventoryTransactionRequest request = InventoryTransactionRequest.builder()
                            .ingredientId(ingredient.getIngredientId())
                            .transactionType("USAGE")
                            .quantity(totalQtyToDeduct)
                            .transactionDate(LocalDateTime.now())
                            .note("Order " + order.getId() + " - " + item.getProductName())
                            .build();
                    
                    inventoryClient.recordTransaction(request);
                    successfulTransactions.add(request); // track for compensation
                }
            }
            
            // Step 3: Confirm Order
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
            log.info("Saga completed successfully for order: {}", order.getId());
            
        } catch (Exception e) {
            log.error("Saga failed for order: {}. Initiating compensation.", order.getId(), e);
            compensate(order, successfulTransactions);
            throw new SagaException("Failed to process order: " + e.getMessage());
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
                List<ProductIngredientResponse> ingredients = inventoryClient.getIngredientsByProductId(item.getProductId());
                
                for (ProductIngredientResponse ingredient : ingredients) {
                    BigDecimal totalQtyToRefund = ingredient.getQuantity().multiply(new BigDecimal(item.getQuantity()));
                    
                    InventoryTransactionRequest revertRequest = InventoryTransactionRequest.builder()
                            .ingredientId(ingredient.getIngredientId())
                            .transactionType("IMPORT") // Refund back to stock
                            .quantity(totalQtyToRefund)
                            .transactionDate(LocalDateTime.now())
                            .note("Refund for cancelled Order " + order.getId())
                            .build();
                    
                    inventoryClient.recordTransaction(revertRequest);
                }
            }
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }
}

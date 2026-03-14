package com.csms.inventory.service;

import com.csms.inventory.dto.TransactionRequest;
import com.csms.inventory.dto.TransactionResponse;
import com.csms.inventory.entity.Ingredient;
import com.csms.inventory.entity.IngredientTransaction;
import com.csms.inventory.exception.ResourceNotFoundException;
import com.csms.inventory.exception.ValidationException;
import com.csms.inventory.repository.IngredientRepository;
import com.csms.inventory.repository.IngredientTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final IngredientTransactionRepository transactionRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByIngredient(Long ingredientId) {
        return transactionRepository.findByIngredientId(ingredientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransactionResponse recordTransaction(TransactionRequest request) {
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + request.getIngredientId()));

        String type = request.getTransactionType().toUpperCase();
        BigDecimal currentStock = ingredient.getCurrentStock();
        BigDecimal quantity = request.getQuantity();

        // Calculate new stock based on transaction type
        if (type.equals("IMPORT")) {
            ingredient.setCurrentStock(currentStock.add(quantity));
        } else if (type.equals("EXPORT") || type.equals("WASTE") || type.equals("USAGE")) {
            if (currentStock.compareTo(quantity) < 0) {
                throw new ValidationException("Not enough stock for ingredient: " + ingredient.getName());
            }
            ingredient.setCurrentStock(currentStock.subtract(quantity));
        } else if (type.equals("ADJUSTMENT")) {
            // For adjustment, quantity can be positive or negative, but we simplify it here
            // If we want negative adjustment, the request should handle it, but our DTO requires > 0
            throw new ValidationException("ADJUSTMENT logic not fully supported here, use IMPORT or EXPORT");
        } else {
            throw new ValidationException("Invalid transaction type: " + type);
        }

        ingredientRepository.save(ingredient);

        BigDecimal unitPrice = request.getUnitPrice() != null ? request.getUnitPrice() : ingredient.getUnitCost();

        IngredientTransaction transaction = IngredientTransaction.builder()
                .ingredient(ingredient)
                .transactionType(type)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .transactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDateTime.now())
                .note(request.getNote())
                .build();

        IngredientTransaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }

    private TransactionResponse mapToResponse(IngredientTransaction transaction) {
        BigDecimal totalValue = transaction.getUnitPrice() != null ? 
            transaction.getQuantity().multiply(transaction.getUnitPrice()) : BigDecimal.ZERO;

        return TransactionResponse.builder()
                .id(transaction.getId())
                .ingredientId(transaction.getIngredient().getId())
                .ingredientName(transaction.getIngredient().getName())
                .transactionType(transaction.getTransactionType())
                .quantity(transaction.getQuantity())
                .unitPrice(transaction.getUnitPrice())
                .totalValue(totalValue)
                .transactionDate(transaction.getTransactionDate())
                .note(transaction.getNote())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}

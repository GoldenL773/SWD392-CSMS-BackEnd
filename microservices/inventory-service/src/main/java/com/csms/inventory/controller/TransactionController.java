package com.csms.inventory.controller;

import com.csms.inventory.dto.TransactionRequest;
import com.csms.inventory.dto.TransactionResponse;
import com.csms.inventory.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByIngredient(@PathVariable Long ingredientId) {
        return ResponseEntity.ok(transactionService.getTransactionsByIngredient(ingredientId));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> recordTransaction(@Valid @RequestBody TransactionRequest request) {
        return new ResponseEntity<>(transactionService.recordTransaction(request), HttpStatus.CREATED);
    }
}

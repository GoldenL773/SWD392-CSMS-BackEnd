package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.request.IngredientRequest;
import fu.se.swd392csms.dto.request.IngredientTransactionRequest;
import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ingredient Controller
 * Handles HTTP requests for ingredient/inventory management
 */
@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredient Management", description = "Ingredient and inventory management APIs")
public class IngredientController {
    
    private final IngredientService ingredientService;
    
    /**
     * Get all ingredients with optional search and pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get all ingredients", description = "Get all ingredients with optional search and pagination")
    public ResponseEntity<Page<IngredientResponse>> getAllIngredients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<IngredientResponse> ingredients = ingredientService.getAllIngredients(search, pageable);
        return ResponseEntity.ok(ingredients);
    }
    
    /**
     * Get ingredient by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get ingredient by ID", description = "Get a specific ingredient by its ID")
    public ResponseEntity<IngredientResponse> getIngredientById(@PathVariable Long id) {
        IngredientResponse ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }
    
    /**
     * Create new ingredient
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new ingredient", description = "Create a new ingredient")
    public ResponseEntity<IngredientResponse> createIngredient(@Valid @RequestBody IngredientRequest request) {
        IngredientResponse ingredient = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredient);
    }
    
    /**
     * Update ingredient
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update ingredient", description = "Update an existing ingredient")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientRequest request) {
        IngredientResponse ingredient = ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(ingredient);
    }
    
    /**
     * Delete ingredient
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete ingredient", description = "Delete an ingredient by ID")
    public ResponseEntity<MessageResponse> deleteIngredient(@PathVariable Long id) {
        MessageResponse response = ingredientService.deleteIngredient(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Record ingredient transaction
     */
    @PostMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Record transaction", description = "Record ingredient import/export transaction")
    public ResponseEntity<IngredientTransactionResponse> recordTransaction(
            @Valid @RequestBody IngredientTransactionRequest request) {
        IngredientTransactionResponse transaction = ingredientService.recordTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    
    /**
     * Get ingredient transactions
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get transactions", description = "Get ingredient transactions with optional filters")
    public ResponseEntity<Page<IngredientTransactionResponse>> getTransactions(
            @RequestParam(required = false) Long ingredientId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<IngredientTransactionResponse> transactions = 
                ingredientService.getTransactions(ingredientId, type, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get low stock ingredients
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get low stock ingredients", description = "Get ingredients below minimum stock level")
    public ResponseEntity<List<IngredientResponse>> getLowStockIngredients() {
        List<IngredientResponse> lowStockIngredients = ingredientService.getLowStockIngredients();
        return ResponseEntity.ok(lowStockIngredients);
    }
}

package fu.se.swd392csms.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fu.se.swd392csms.dto.request.IngredientRequest;
import fu.se.swd392csms.dto.request.IngredientTransactionRequest;
import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import fu.se.swd392csms.dto.response.MessageResponse;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.entity.Ingredient;
import fu.se.swd392csms.entity.IngredientTransaction;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.EmployeeRepository;
import fu.se.swd392csms.repository.IngredientRepository;
import fu.se.swd392csms.repository.IngredientTransactionRepository;
import fu.se.swd392csms.service.IngredientService;
import lombok.RequiredArgsConstructor;

/**
 * Ingredient Service Implementation
 * Handles business logic for ingredient/inventory management
 */
@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {
    
    private final IngredientRepository ingredientRepository;
    private final IngredientTransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * Create a new ingredient
     * @param request Ingredient creation request
     * @return Created ingredient response
     */
    @Override
    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        // Check if ingredient name already exists
        if (ingredientRepository.existsByName(request.getName())) {
            throw new BadRequestException("Ingredient with name '" + request.getName() + "' already exists");
        }
        
        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());
        ingredient.setUnit(request.getUnit());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setMinimumStock(request.getMinimumStock());
        ingredient.setPricePerUnit(request.getPricePerUnit());

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        
        return convertToIngredientResponse(savedIngredient);
    }
    
    /**
     * Get ingredient by ID
     * @param id Ingredient ID
     * @return Ingredient response
     */
    @Override
    public IngredientResponse getIngredientById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
        return convertToIngredientResponse(ingredient);
    }
    
    /**
     * Get all ingredients with optional search and pagination
     * @param search Optional search term
     * @param pageable Pagination parameters
     * @return Page of ingredients
     */
    @Override
    public Page<IngredientResponse> getAllIngredients(String search, Pageable pageable) {
        Page<Ingredient> ingredientPage;
        
        if (search != null && !search.isEmpty()) {
            ingredientPage = ingredientRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            ingredientPage = ingredientRepository.findAll(pageable);
        }
        
        List<IngredientResponse> ingredientResponses = ingredientPage.getContent().stream()
                .map(this::convertToIngredientResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(ingredientResponses, pageable, ingredientPage.getTotalElements());
    }
    
    /**
     * Update ingredient
     * @param id Ingredient ID
     * @param request Ingredient update request
     * @return Updated ingredient response
     */
    @Override
    @Transactional
    public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
        
        // Check name uniqueness if changed
        if (!ingredient.getName().equals(request.getName()) && 
            ingredientRepository.existsByName(request.getName())) {
            throw new BadRequestException("Ingredient with name '" + request.getName() + "' already exists");
        }
        
        ingredient.setName(request.getName());
        ingredient.setUnit(request.getUnit());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setMinimumStock(request.getMinimumStock());
        ingredient.setPricePerUnit(request.getPricePerUnit());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        
        return convertToIngredientResponse(updatedIngredient);
    }
    
    /**
     * Delete ingredient
     * @param id Ingredient ID
     * @return Success message
     */
    @Override
    @Transactional
    public MessageResponse deleteIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
        
        ingredientRepository.delete(ingredient);
        
        return new MessageResponse("Ingredient deleted successfully");
    }
    
    /**
     * Record ingredient transaction (import/export)
     * @param request Transaction request
     * @return Created transaction response
     */
    @Override
    @Transactional
    public IngredientTransactionResponse recordTransaction(IngredientTransactionRequest request) {
        // Validate ingredient exists
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", request.getIngredientId()));
        
        // Validate employee exists
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        // Validate transaction type
        String type = request.getType().toUpperCase();
        if (!type.equals("IMPORT") && !type.equals("EXPORT")) {
            throw new BadRequestException("Transaction type must be either IMPORT or EXPORT");
        }
        
        // Update ingredient quantity
        if (type.equals("IMPORT")) {
            ingredient.setQuantity(ingredient.getQuantity().add(request.getQuantity()));
        } else { // EXPORT
            if (ingredient.getQuantity().compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException(
                        "Insufficient stock for ingredient '" + ingredient.getName() + 
                        "'. Available: " + ingredient.getQuantity() + ", Requested: " + request.getQuantity()
                );
            }
            ingredient.setQuantity(ingredient.getQuantity().subtract(request.getQuantity()));
        }
        
        ingredientRepository.save(ingredient);
        
        // Create transaction record
        IngredientTransaction transaction = new IngredientTransaction();
        transaction.setIngredient(ingredient);
        transaction.setType(type);
        transaction.setQuantity(request.getQuantity());
        transaction.setEmployee(employee);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setNotes(request.getNotes());
        
        IngredientTransaction savedTransaction = transactionRepository.save(transaction);
        
        return convertToTransactionResponse(savedTransaction, ingredient, employee);
    }
    
    /**
     * Get ingredient transactions
     * @param ingredientId Optional ingredient ID filter
     * @param type Optional transaction type filter (IMPORT/EXPORT)
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    @Override
    public Page<IngredientTransactionResponse> getTransactions(Long ingredientId, String type, Pageable pageable) {
        List<IngredientTransaction> transactions;
        
        if (ingredientId != null && type != null) {
            transactions = transactionRepository.findByIngredientIdAndType(ingredientId, type.toUpperCase());
        } else if (ingredientId != null) {
            transactions = transactionRepository.findByIngredientId(ingredientId);
        } else if (type != null) {
            transactions = transactionRepository.findByType(type.toUpperCase());
        } else {
            transactions = transactionRepository.findAll();
        }
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), transactions.size());
        
        List<IngredientTransactionResponse> transactionResponses = transactions.subList(start, end).stream()
                .map(transaction -> convertToTransactionResponse(
                        transaction, 
                        transaction.getIngredient(), 
                        transaction.getEmployee()))
                .collect(Collectors.toList());
        
        return new PageImpl<>(transactionResponses, pageable, transactions.size());
    }
    
    /**
     * Get low stock ingredients
     * @return List of ingredients below minimum stock
     */
    @Override
    public List<IngredientResponse> getLowStockIngredients() {
        List<Ingredient> lowStockIngredients = ingredientRepository.findAll().stream()
                .filter(ingredient -> ingredient.getQuantity().compareTo(ingredient.getMinimumStock()) < 0)
                .collect(Collectors.toList());
        
        return lowStockIngredients.stream()
                .map(this::convertToIngredientResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Ingredient entity to IngredientResponse DTO
     */
    private IngredientResponse convertToIngredientResponse(Ingredient ingredient) {
        boolean isLowStock = ingredient.getQuantity().compareTo(ingredient.getMinimumStock()) < 0;

        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .quantity(ingredient.getQuantity())
                .minimumStock(ingredient.getMinimumStock())
                .pricePerUnit(ingredient.getPricePerUnit() != null ? ingredient.getPricePerUnit() : BigDecimal.ZERO)
                .isLowStock(isLowStock)
                .build();
    }
    
    /**
     * Convert IngredientTransaction entity to IngredientTransactionResponse DTO
     */
    private IngredientTransactionResponse convertToTransactionResponse(
            IngredientTransaction transaction, Ingredient ingredient, Employee employee) {
        return IngredientTransactionResponse.builder()
                .id(transaction.getId())
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .type(transaction.getType())
                .quantity(transaction.getQuantity())
                .employeeId(employee.getId())
                .employeeName(employee.getFullName())
                .transactionDate(transaction.getTransactionDate())
                .notes(transaction.getNotes())
                .build();
    }
}

package fu.se.swd392csms.service.impl;

import fu.se.swd392csms.dto.request.IngredientTransactionRequest;
import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.entity.Ingredient;
import fu.se.swd392csms.entity.IngredientTransaction;
import fu.se.swd392csms.exception.BadRequestException;
import fu.se.swd392csms.exception.ResourceNotFoundException;
import fu.se.swd392csms.repository.EmployeeRepository;
import fu.se.swd392csms.repository.IngredientRepository;
import fu.se.swd392csms.repository.IngredientRepository_v1;
import fu.se.swd392csms.repository.IngredientTransactionRepository;
import fu.se.swd392csms.service.IngredientService_v1;
import fu.se.swd392csms.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl_v1 implements IngredientService_v1 {

    private final IngredientRepository_v1 ingredientRepositoryV1;
    private final IngredientTransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Page<IngredientResponse> getAllIngredients(String search, Pageable pageable) {
        Page<Ingredient> ingredientPage;

        if (search != null && !search.isEmpty()) {
            ingredientPage = ingredientRepositoryV1.findByNameContainingIgnoreCase(search, pageable);
        } else {
            ingredientPage = ingredientRepositoryV1.findAll(pageable);
        }

        List<IngredientResponse> ingredientResponses = ingredientPage.getContent().stream()
                .map((content)-> { return Mapper.mapEntityToDTO(content);})
                .collect(Collectors.toList());

        return new PageImpl<>(ingredientResponses, pageable, ingredientPage.getTotalElements());
    }

    @Override
    public IngredientTransactionResponse recordTransaction(IngredientTransactionRequest request) {

        Ingredient ingredient = ingredientRepositoryV1.findById(request.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Ingredient", "with ID" ,request.getIngredientId()));


        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found Employee", "wiht ID", request.getEmployeeId()));

        String type = request.getType().toUpperCase();
        if (!type.equals("IMPORT") && !type.equals("EXPORT")) {
            throw new BadRequestException("Transaction type must be either IMPORT or EXPORT");
        }

        if (type.equals("IMPORT")) {
            ingredient.setQuantity(ingredient.getQuantity().add(request.getQuantity()));
        } else {
            if (ingredient.getQuantity().compareTo(request.getQuantity()) < 0) {
                throw new BadRequestException(
                        "Insufficient stock for ingredient '" + ingredient.getName() +
                                "'. Available: " + ingredient.getQuantity() + ", Requested: " + request.getQuantity()
                );
            }
            ingredient.setQuantity(ingredient.getQuantity().subtract(request.getQuantity()));
        }

        ingredientRepositoryV1.save(ingredient);

        IngredientTransaction ingredientTransaction = IngredientTransaction.builder()
                .ingredient(ingredient)
                .type(type)
                .transactionDate(LocalDateTime.now())
                .quantity(request.getQuantity())
                .employee(employee)
                .notes(request.getNotes())
                .build();

        IngredientTransaction savedTransaction = transactionRepository.save(ingredientTransaction);

        return Mapper.mapEntityToDTO(savedTransaction, ingredient, employee);
    }

}

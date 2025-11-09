package fu.se.swd392csms.controller;

import fu.se.swd392csms.dto.request.IngredientTransactionRequest;
import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import fu.se.swd392csms.service.IngredientService_v1;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController_v1 {

    private final IngredientService_v1 ingredientServiceV1;
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Page<IngredientResponse>> getAllIngredients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<IngredientResponse> ingredients = ingredientServiceV1.getAllIngredients(search, pageable);
        return ResponseEntity.ok(ingredients);
    }


    @PostMapping("/transactions")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<IngredientTransactionResponse> recordTransaction(
            @Valid @RequestBody IngredientTransactionRequest request) {
        IngredientTransactionResponse transaction = ingredientServiceV1.recordTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
}

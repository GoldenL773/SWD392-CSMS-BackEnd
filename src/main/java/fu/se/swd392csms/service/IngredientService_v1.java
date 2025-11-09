package fu.se.swd392csms.service;

import fu.se.swd392csms.dto.request.IngredientTransactionRequest;
import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IngredientService_v1 {
    Page<IngredientResponse> getAllIngredients(String search, Pageable pageable);
    IngredientTransactionResponse recordTransaction(IngredientTransactionRequest request);
}

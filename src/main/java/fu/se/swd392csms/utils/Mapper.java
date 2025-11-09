package fu.se.swd392csms.utils;

import fu.se.swd392csms.dto.response.IngredientResponse;
import fu.se.swd392csms.dto.response.IngredientTransactionResponse;
import fu.se.swd392csms.entity.Employee;
import fu.se.swd392csms.entity.Ingredient;
import fu.se.swd392csms.entity.IngredientTransaction;

import java.math.BigDecimal;

public class Mapper {
    public static IngredientResponse mapEntityToDTO(Ingredient ingredient){
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .quantity(ingredient.getQuantity())
                .minimumStock(ingredient.getMinimumStock())
                .pricePerUnit(ingredient.getPricePerUnit() != null ? ingredient.getPricePerUnit() : BigDecimal.ZERO)
                .build();
    }

    public static IngredientTransactionResponse mapEntityToDTO(IngredientTransaction transaction,
                                                               Ingredient ingredient,
                                                               Employee employee){
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

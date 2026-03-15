package com.csms.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {
    // Allowed to be null in request, injected by controller from header
    private Long userId;

    private String note;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    @com.fasterxml.jackson.annotation.JsonAlias("orderItems")
    private List<OrderItemRequest> items;

    private Long promotionId;
    private String employeeName;
    private java.math.BigDecimal totalAmount;
}

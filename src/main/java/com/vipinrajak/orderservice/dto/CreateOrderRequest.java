package com.vipinrajak.orderservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "customerId is required") String customerId,
        @NotBlank(message = "productId is required") String productId,
        @Min(value = 1, message = "quantity must be at least 1") int quantity,
        @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "totalAmount must be positive") BigDecimal totalAmount
) {
}

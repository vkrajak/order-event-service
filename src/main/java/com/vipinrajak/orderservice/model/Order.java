package com.vipinrajak.orderservice.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Minimal order representation. In v1 this is held in memory / logged only —
 * persistence (Postgres) is on the roadmap, see README "Next steps".
 */
public record Order(
        UUID orderId,
        String customerId,
        String productId,
        int quantity,
        BigDecimal totalAmount,
        Instant createdAt
) {
    public static Order create(String customerId, String productId, int quantity, BigDecimal totalAmount) {
        return new Order(UUID.randomUUID(), customerId, productId, quantity, totalAmount, Instant.now());
    }
}

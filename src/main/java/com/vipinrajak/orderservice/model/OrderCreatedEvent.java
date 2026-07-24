package com.vipinrajak.orderservice.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * The event schema published to the "order.created" topic.
 *
 * eventId is intentionally separate from orderId: eventId uniquely identifies
 * THIS publication of the event (used for consumer-side idempotency / dedup),
 * while orderId identifies the business entity. A retried publish for the same
 * order would carry a new eventId but the same orderId — that distinction is
 * what allows safe at-least-once delivery semantics downstream.
 */
public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String customerId,
        String productId,
        int quantity,
        BigDecimal totalAmount,
        Instant occurredAt
) {
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                order.orderId(),
                order.customerId(),
                order.productId(),
                order.quantity(),
                order.totalAmount(),
                Instant.now()
        );
    }
}

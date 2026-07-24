package com.vipinrajak.orderservice.kafka;

import com.vipinrajak.orderservice.model.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulates an "inventory" service reacting to order-created events.
 *
 * IDEMPOTENCY NOTE: Kafka's default delivery guarantee is at-least-once —
 * a consumer rebalance, retry, or redeploy can cause the same message to be
 * delivered more than once. Without a dedup check, that means double-reserving
 * stock for the same order. This consumer tracks processed eventIds to make
 * reprocessing a no-op.
 *
 * The in-memory Set below is intentionally a v1 placeholder — it does not
 * survive a restart and does not work across multiple consumer instances.
 * The production-correct version of this check belongs in Redis (SETNX with
 * a TTL) or a unique-constraint table in the database. Documented here on
 * purpose as an honest "known limitation," not hidden.
 */
@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);

    // v1 placeholder for a distributed idempotency store (see class javadoc).
    private final Set<UUID> processedEventIds = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        if (!processedEventIds.add(event.eventId())) {
            log.warn("Duplicate delivery detected for eventId={}, skipping reservation for orderId={}",
                    event.eventId(), event.orderId());
            return;
        }

        reserveInventory(event);
    }

    private void reserveInventory(OrderCreatedEvent event) {
        // Placeholder for real inventory logic (DB update, stock check, etc.)
        log.info("Reserved {} unit(s) of productId={} for orderId={} (customerId={})",
                event.quantity(), event.productId(), event.orderId(), event.customerId());
    }
}

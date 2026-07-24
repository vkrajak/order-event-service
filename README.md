<<<<<<< HEAD
# Order Event Service

An event-driven order processing service built with **Java, Spring Boot, and Apache Kafka**.
A client creates an order over REST → an `OrderCreatedEvent` is published to Kafka →
a downstream consumer reserves inventory asynchronously, with duplicate-delivery protection.

> **Status: early / in progress.** This is v1 — a real, running skeleton, not a finished
> product. See [Next Steps](#next-steps) for what's deliberately not built yet.

## Why this project

Most "microservices demo" repos wire a producer to a consumer and stop there. The point of
this one is to show the parts that actually matter in production: partitioning strategy,
ordering guarantees, and idempotent consumption — because Kafka's at-least-once delivery
means duplicate messages *will* happen, and a service that isn't built to handle that will
double-charge, double-ship, or double-reserve stock under real load.

## Architecture

```
                 POST /api/orders
                        │
                        ▼
              ┌───────────────────┐
              │  OrderController   │
              └─────────┬──────────┘
                        │
                        ▼
              ┌───────────────────┐
              │    OrderService    │
              └─────────┬──────────┘
                        │ publish(OrderCreatedEvent)
                        ▼
              ┌───────────────────┐
              │ OrderEventProducer │──── key = orderId (preserves per-order ordering)
              └─────────┬──────────┘
                        ▼
        ┌───────────────────────────────┐
        │   Kafka topic: order.created   │
        │        (3 partitions)          │
        └───────────────┬────────────────┘
                        ▼
              ┌────────────────────────┐
              │ InventoryEventConsumer  │──── dedups by eventId before acting
              │   (group: inventory-    │
              │        service)         │
              └────────────────────────┘
```

### Key design decisions

- **`orderId` vs `eventId`** — the event carries both. `orderId` identifies the business
  entity; `eventId` identifies *this specific publication* of an event about that entity.
  That separation is what makes idempotent consumption possible: a redelivered message has
  a repeated `eventId`, which the consumer can recognize and skip.
- **Partition key = `orderId`** — all events for the same order land on the same partition,
  so per-order ordering is preserved even with multiple partitions and multiple consumer
  instances.
- **`acks=all` on the producer** — favors durability over raw throughput; an order event
  is not something that's acceptable to silently lose.
- **In-memory idempotency store (v1 limitation, intentional)** — `InventoryEventConsumer`
  currently tracks processed `eventId`s in a `ConcurrentHashMap`-backed set. This works for
  a single instance but does **not** survive a restart and does **not** work across multiple
  running instances of the consumer. That's a known, documented limitation for v1 — see
  Next Steps.

## Tech stack

Java 17 · Spring Boot 3 · Spring Kafka · Maven · Docker Compose · GitHub Actions

## Running it locally

**Prerequisites:** Docker, Java 17, Maven

```bash
# 1. Start Kafka + Zookeeper + Kafka UI
docker compose up -d

# 2. Run the service
mvn spring-boot:run

# 3. Create an order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
        "customerId": "cust-001",
        "productId": "prod-42",
        "quantity": 2,
        "totalAmount": 49.99
      }'

# 4. Watch the console log — you'll see the producer publish the event
#    and the consumer reserve inventory in response.
```

Kafka UI is available at `http://localhost:8081` to inspect topics, partitions, and messages directly.

## Next steps

Being upfront about what's not built yet, in priority order:

- [ ] Replace the in-memory idempotency set with Redis (`SETNX` + TTL) so dedup survives
      restarts and works across multiple consumer instances
- [ ] Persist orders to PostgreSQL instead of holding them only in the request/response cycle
- [ ] Add a dead-letter topic for events that fail processing after retries
- [ ] Add integration tests using Testcontainers (embedded Kafka + Postgres)
- [ ] Add request-level observability (Micrometer + Prometheus) on top of the existing
      Actuator health endpoint

## Author

**Vipin Rajak** — Backend Developer (Java, Spring Boot, Microservices, Kafka, AWS)
[LinkedIn](https://www.linkedin.com/in/vipin-rajak-a6b0231b3) · vipinkumarrajak.vr@gmail.com

## License

MIT — see [LICENSE](LICENSE)
=======
# order-event-service
Event-driven order processing service - Java, Spring boot, Kafka
>>>>>>> origin/main

package com.vipinrajak.orderservice.kafka;

import com.vipinrajak.orderservice.model.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.vipinrajak.orderservice.config.KafkaTopicConfig.ORDER_CREATED_TOPIC;

@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Keying by orderId (not eventId) ensures every event for the same order
     * lands on the same partition, preserving per-order ordering guarantees
     * even though the topic has multiple partitions.
     */
    public void publish(OrderCreatedEvent event) {
        String key = event.orderId().toString();
        kafkaTemplate.send(ORDER_CREATED_TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish OrderCreatedEvent for orderId={}", event.orderId(), ex);
                    } else {
                        log.info("Published OrderCreatedEvent eventId={} orderId={} partition={} offset={}",
                                event.eventId(), event.orderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}

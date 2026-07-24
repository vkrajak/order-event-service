package com.vipinrajak.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_CREATED_TOPIC = "order.created";

    /**
     * 3 partitions so the topic can be consumed in parallel by multiple
     * instances of a consumer group — a deliberate nod to horizontal scaling,
     * not just a toy single-partition setup.
     */
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

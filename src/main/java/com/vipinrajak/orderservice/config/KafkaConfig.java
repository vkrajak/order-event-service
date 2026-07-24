package com.vipinrajak.orderservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

/**
 * Explicit producer/consumer factory beans.
 * <p>
 * Spring Boot's Kafka autoconfiguration provides a default KafkaTemplate typed
 * as KafkaTemplate<Object, Object>. Java generics are invariant, so injecting a
 * more specific KafkaTemplate<String, Object> at a constructor param is NOT
 * guaranteed to resolve against that default bean via type-based autowiring.
 * Declaring the factories explicitly here, built from Spring Boot's own
 * KafkaProperties (so application.yml settings like acks/bootstrap-servers
 * still apply), avoids that mismatch instead of relying on autoconfiguration
 * generics lining up by luck.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        var props = kafkaProperties.buildProducerProperties(null);
        props.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
        var props = kafkaProperties.buildConsumerProperties(null);
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}

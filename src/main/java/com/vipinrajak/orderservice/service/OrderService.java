package com.vipinrajak.orderservice.service;

import com.vipinrajak.orderservice.dto.CreateOrderRequest;
import com.vipinrajak.orderservice.kafka.OrderEventProducer;
import com.vipinrajak.orderservice.model.Order;
import com.vipinrajak.orderservice.model.OrderCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderEventProducer producer;

    public OrderService(OrderEventProducer producer) {
        this.producer = producer;
    }

    public Order createOrder(CreateOrderRequest request) {
        Order order = Order.create(
                request.customerId(),
                request.productId(),
                request.quantity(),
                request.totalAmount()
        );

        OrderCreatedEvent event = OrderCreatedEvent.from(order);
        producer.publish(event);

        return order;
    }
}

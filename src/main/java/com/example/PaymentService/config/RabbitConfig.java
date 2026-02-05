package com.example.PaymentService.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_QUEUE = "payment.process";
    public static final String PAYMENT_ROUTING_KEY = "payment.key";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(PAYMENT_ROUTING_KEY);
    }
}

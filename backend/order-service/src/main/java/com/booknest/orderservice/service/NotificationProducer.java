package com.booknest.orderservice.service;

import com.booknest.orderservice.config.RabbitMQConfig;
import com.booknest.orderservice.dto.OrderNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(OrderNotification data) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.QUEUE_NAME,
                data
        );
    }
}
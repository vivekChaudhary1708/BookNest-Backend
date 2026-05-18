package com.booknest.notificationservice.service;

import com.booknest.notificationservice.config.RabbitMQConfig;
import com.booknest.notificationservice.dto.OrderNotification;
import com.booknest.notificationservice.model.NotificationEntry;
import com.booknest.notificationservice.repository.NotificationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;

    public NotificationService(RabbitTemplate rabbitTemplate,
                               NotificationRepository notificationRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationRepository = notificationRepository;
    }

    public String sendNotification(OrderNotification data) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, data);
        return "Message Sent Successfully";
    }

    public NotificationEntry saveNotification(OrderNotification data) {
        NotificationEntry entry = new NotificationEntry();
        entry.setOrderId(data.getOrderId());
        entry.setTitle(data.getTitle() == null || data.getTitle().isBlank() ? "BookNest Notification" : data.getTitle());
        entry.setMessage(data.getMessage());
        entry.setRecipientType(data.getRecipientType() == null ? "CUSTOMER" : data.getRecipientType());
        entry.setRecipientId(data.getRecipientId());
        entry.setUserEmail(data.getUserEmail());
        entry.setRead(false);
        entry.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(entry);
    }

    public List<NotificationEntry> getNotifications(String recipientType, String recipientId) {
        if (recipientId == null || recipientId.isBlank()) {
            return notificationRepository.findByRecipientTypeOrderByCreatedAtDesc(recipientType);
        }
        return notificationRepository.findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(recipientType, recipientId);
    }
}
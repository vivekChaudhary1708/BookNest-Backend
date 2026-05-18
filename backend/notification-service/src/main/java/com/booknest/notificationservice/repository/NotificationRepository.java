package com.booknest.notificationservice.repository;

import com.booknest.notificationservice.model.NotificationEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationEntry, String> {
    List<NotificationEntry> findByRecipientTypeOrderByCreatedAtDesc(String recipientType);
    List<NotificationEntry> findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(String recipientType, String recipientId);
}

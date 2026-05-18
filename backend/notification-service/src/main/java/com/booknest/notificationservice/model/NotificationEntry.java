package com.booknest.notificationservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class NotificationEntry {
    @Id
    private String id;
    private String orderId;
    private String title;
    private String message;
    private String recipientType;
    private String recipientId;
    private String userEmail;
    private boolean read;
    private LocalDateTime createdAt;
}

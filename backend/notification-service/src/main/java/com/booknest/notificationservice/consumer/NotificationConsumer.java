package com.booknest.notificationservice.consumer;

import com.booknest.notificationservice.dto.OrderNotification;
import com.booknest.notificationservice.service.EmailService;
import com.booknest.notificationservice.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;

    public NotificationConsumer(EmailService emailService,
                                NotificationService notificationService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "order.notification.queue")
    public void receiveNotification(OrderNotification data) {

        System.out.println("📩 New Notification Received");
        System.out.println("Order ID: " + data.getOrderId());
        System.out.println("User Email: " + data.getUserEmail());
        System.out.println("Message: " + data.getMessage());

        notificationService.saveNotification(data);

        if (data.getUserEmail() != null && !data.getUserEmail().isBlank()) {
            try {
                emailService.sendEmail(
                        data.getUserEmail(),
                        data.getTitle() == null ? "BookNest Order Update" : data.getTitle(),
                        "Hello,\n\n" +
                                data.getMessage() +
                                "\nOrder ID: " + data.getOrderId() +
                                "\n\nThank you for shopping with BookNest."
                );
            } catch (Exception ex) {
                System.out.println("Email delivery failed for " + data.getUserEmail() + ": " + ex.getMessage());
            }
        }
    }
}
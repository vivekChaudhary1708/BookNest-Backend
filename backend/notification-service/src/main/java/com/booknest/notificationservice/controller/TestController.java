package com.booknest.notificationservice.controller;

import com.booknest.notificationservice.dto.OrderNotification;
import com.booknest.notificationservice.model.NotificationEntry;
import com.booknest.notificationservice.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class TestController {

    private final NotificationService notificationService;

    public TestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestBody OrderNotification data) {
        return notificationService.sendNotification(data);
    }

    @GetMapping
    public List<NotificationEntry> getNotifications(@RequestParam String recipientType,
                                                    @RequestParam(required = false) String recipientId) {
        return notificationService.getNotifications(recipientType, recipientId);
    }
}
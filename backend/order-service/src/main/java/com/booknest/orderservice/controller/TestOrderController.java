package com.booknest.orderservice.controller;

import com.booknest.orderservice.dto.OrderNotification;
import com.booknest.orderservice.service.NotificationProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class TestOrderController {

    private final NotificationProducer notificationProducer;

    public TestOrderController(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    @PostMapping("/send")
    public String sendOrder(@RequestBody OrderNotification data) {

        notificationProducer.sendNotification(data);

        return "Order Notification Sent Successfully";
    }
}
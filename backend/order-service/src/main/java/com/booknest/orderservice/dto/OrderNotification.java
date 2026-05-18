package com.booknest.orderservice.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderNotification implements Serializable {

    private String orderId;
    private String userEmail;
    private String recipientType;
    private String recipientId;
    private String title;
    private String message;
}
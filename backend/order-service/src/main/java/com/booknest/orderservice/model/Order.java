package com.booknest.orderservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
public class Order {

    @Id
    private String id;

    private String userId;
    private String userName;
    private String userEmail;
    private String paymentMethod;

    private String productId;

    private String productName;

    private double price;

    private int quantity;

    private double totalAmount;

    private String status;
    private String refundStatus;
    private double refundAmount;

    private LocalDateTime orderDate;
    private LocalDateTime cancelledAt;

    private List<OrderItem> items;

    private String paymentId;
}
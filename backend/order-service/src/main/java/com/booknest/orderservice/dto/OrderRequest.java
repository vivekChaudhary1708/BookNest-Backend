package com.booknest.orderservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private String userId;
    private String userName;
    private String userEmail;
    private String paymentMethod;

    private String productId;

    private String productName;

    private double price;

    private int quantity;

    private double totalAmount;

    private List<OrderItemRequest> items;

    private String paymentId;
}
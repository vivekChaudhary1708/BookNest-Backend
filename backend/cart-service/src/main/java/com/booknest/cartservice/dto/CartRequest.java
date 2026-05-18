package com.booknest.cartservice.dto;

import lombok.Data;

@Data
public class CartRequest {

    private String userId;

    private String productId;

    private String productName;

    private double price;

    private int quantity;

    private String imageUrl;
}
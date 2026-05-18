package com.booknest.wishlistservice.dto;

import lombok.Data;

@Data
public class WishlistRequest {

    private String userId;

    private String productId;

    private String productName;

    private double price;
}
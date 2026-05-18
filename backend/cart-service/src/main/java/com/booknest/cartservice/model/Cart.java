package com.booknest.cartservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart")
@Data
public class Cart {

    @Id
    private String id;

    private String userId;

    private String productId;

    private String productName;

    private double price;

    private int quantity;

    private String imageUrl;
}
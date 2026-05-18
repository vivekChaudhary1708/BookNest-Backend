package com.booknest.wishlistservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wishlist")
@Data
public class Wishlist {

    @Id
    private String id;

    private String userId;

    private String productId;

    private String productName;

    private double price;
}
package com.booknest.reviewservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
@Data
public class Review {

    @Id
    private String id;

    private String userId;

    private String productId;

    private String userName;

    private int rating;

    private String comment;
}
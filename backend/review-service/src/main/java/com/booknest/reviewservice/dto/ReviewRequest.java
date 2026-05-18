package com.booknest.reviewservice.dto;

import lombok.Data;

@Data
public class ReviewRequest {

    private String userId;

    private String productId;

    private String userName;

    private int rating;

    private String comment;
}
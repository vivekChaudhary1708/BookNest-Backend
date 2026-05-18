package com.booknest.productservice.dto;

import lombok.Data;

@Data
public class ProductRequest {

    private String name;

    private String description;

    private double price;

    private String category;

    private int stock;

    private String imageUrl;
}
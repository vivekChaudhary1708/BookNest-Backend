package com.booknest.reviewservice.controller;

import com.booknest.reviewservice.dto.ReviewRequest;
import com.booknest.reviewservice.model.Review;
import com.booknest.reviewservice.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public String addReview(@RequestBody ReviewRequest request) {
        return reviewService.addReview(request);
    }

    @GetMapping("/product/{productId}")
    public List<Review> getProductReviews(@PathVariable String productId) {
        return reviewService.getProductReviews(productId);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReview(@PathVariable String id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/rating/{productId}")
    public double getAverageRating(@PathVariable String productId) {
        return reviewService.getAverageRating(productId);
    }
}
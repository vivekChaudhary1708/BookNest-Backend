package com.booknest.reviewservice.service;

import com.booknest.reviewservice.dto.ReviewRequest;
import com.booknest.reviewservice.model.Review;
import com.booknest.reviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public String addReview(ReviewRequest request) {

        Review review = new Review();

        review.setUserId(request.getUserId());
        review.setProductId(request.getProductId());
        review.setUserName(request.getUserName());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);

        return "Review Added Successfully";
    }

    public List<Review> getProductReviews(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public String deleteReview(String id) {
        reviewRepository.deleteById(id);
        return "Review Deleted Successfully";
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public double getAverageRating(String productId) {

        List<Review> reviews = reviewRepository.findByProductId(productId);

        if (reviews.isEmpty()) {
            return 0;
        }

        int total = 0;

        for (Review review : reviews) {
            total += review.getRating();
        }

        return (double) total / reviews.size();
    }
}
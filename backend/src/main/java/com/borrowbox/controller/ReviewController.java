package com.borrowbox.controller;

import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.dto.review.ReviewDto;
import com.borrowbox.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<ReviewDto>> createReview(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewDto.Request request) {
        ReviewDto review = reviewService.createReview(bookingId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Review posted successfully", review));
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<ApiResponse<List<ReviewDto>>> getEquipmentReviews(@PathVariable Long equipmentId) {
        List<ReviewDto> reviews = reviewService.getEquipmentReviews(equipmentId);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}

package com.borrowbox.service;

import com.borrowbox.dto.review.ReviewDto;
import com.borrowbox.entity.Booking;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.Review;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.BookingStatus;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.ReviewRepository;
import com.borrowbox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository, EquipmentRepository equipmentRepository, UserRepository userRepository, AuthService authService) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public ReviewDto createReview(Long bookingId, String renterEmail, ReviewDto.Request request) {
        if (request == null) {
            throw new BadRequestException("Review payload is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        User renter = userRepository.findByEmail(renterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!booking.getRenter().getId().equals(renter.getId())) {
            throw new UnauthorizedAccessException("Only the renter of this booking can post a review!");
        }

        if (booking.getBookingStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("You can only review equipment after the rental has been completed!");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Review rating must be between 1 and 5.");
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new BadRequestException("You have already reviewed this completed rental booking!");
        }

        Review review = Review.builder()
                .booking(booking)
                .equipment(booking.getEquipment())
                .renter(renter)
                .rating(request.getRating())
                .comment(request.getComment() != null ? request.getComment().trim() : null)
                .build();

        review = reviewRepository.save(review);

        // Update Equipment average rating and count
        Equipment equipment = booking.getEquipment();
        Double avg = reviewRepository.calculateAverageRating(equipment.getId());
        long count = reviewRepository.countByEquipmentId(equipment.getId());

        equipment.setRatingAverage(avg != null ? BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        equipment.setReviewCount((int) count);
        equipmentRepository.save(equipment);

        return mapToDto(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getEquipmentReviews(Long equipmentId) {
        return reviewRepository.findByEquipmentIdOrderByCreatedAtDesc(equipmentId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ReviewDto mapToDto(Review r) {
        return ReviewDto.builder()
                .id(r.getId())
                .bookingId(r.getBooking().getId())
                .equipmentId(r.getEquipment().getId())
                .renter(authService.mapToUserDto(r.getRenter()))
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

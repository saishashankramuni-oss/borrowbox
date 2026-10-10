package com.borrowbox;

import com.borrowbox.dto.review.ReviewDto;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.Booking;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.Review;
import com.borrowbox.entity.User;
import com.borrowbox.entity.Wishlist;
import com.borrowbox.entity.enums.BookingStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.ReviewRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.repository.WishlistRepository;
import com.borrowbox.service.AuthService;
import com.borrowbox.service.EquipmentService;
import com.borrowbox.service.ReviewService;
import com.borrowbox.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewAndWishlistTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private ReviewService reviewService;

    private WishlistService wishlistService;

    private User owner;
    private User renter;
    private Equipment equipment;

    @BeforeEach
    void setUp() {
        wishlistService = new WishlistService(wishlistRepository, equipmentRepository, userRepository, equipmentService);

        owner = User.builder()
                .id(1L)
                .email("owner@borrowbox.com")
                .role(Role.ROLE_OWNER)
                .build();

        renter = User.builder()
                .id(2L)
                .fullName("Alex Renter")
                .email("alex.renter@borrowbox.com")
                .role(Role.ROLE_RENTER)
                .build();

        equipment = Equipment.builder()
                .id(10L)
                .owner(owner)
                .name("Sony Alpha A7 IV")
                .dailyPrice(new BigDecimal("2500.00"))
                .ratingAverage(BigDecimal.ZERO)
                .reviewCount(0)
                .build();
    }

    @Test
    @DisplayName("Review succeeds for completed rental by eligible renter and updates equipment average")
    void testCreateReviewSuccess() {
        Booking booking = Booking.builder()
                .id(100L)
                .equipment(equipment)
                .renter(renter)
                .owner(owner)
                .bookingStatus(BookingStatus.COMPLETED)
                .build();

        ReviewDto.Request request = new ReviewDto.Request();
        request.setRating(5);
        request.setComment("Excellent gear, pristine condition!");

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("alex.renter@borrowbox.com")).thenReturn(Optional.of(renter));
        when(reviewRepository.existsByBookingId(100L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review r = i.getArgument(0);
            r.setId(5L);
            return r;
        });
        when(reviewRepository.calculateAverageRating(10L)).thenReturn(5.0);
        when(reviewRepository.countByEquipmentId(10L)).thenReturn(1L);
        when(authService.mapToUserDto(renter)).thenReturn(UserDto.builder().id(2L).fullName("Alex Renter").build());

        ReviewDto reviewDto = reviewService.createReview(100L, "alex.renter@borrowbox.com", request);

        assertNotNull(reviewDto);
        assertEquals(5, reviewDto.getRating());
        assertEquals("Excellent gear, pristine condition!", reviewDto.getComment());
        verify(equipmentRepository).save(argThat(e ->
                e.getRatingAverage().compareTo(new BigDecimal("5.00")) == 0 && e.getReviewCount() == 1
        ));
    }

    @Test
    @DisplayName("Review rejected if booking is not yet completed")
    void testReviewIncompleteBookingRejected() {
        Booking booking = Booking.builder()
                .id(100L)
                .equipment(equipment)
                .renter(renter)
                .owner(owner)
                .bookingStatus(BookingStatus.ACTIVE) // Not COMPLETED
                .build();

        ReviewDto.Request request = new ReviewDto.Request();
        request.setRating(5);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("alex.renter@borrowbox.com")).thenReturn(Optional.of(renter));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                reviewService.createReview(100L, "alex.renter@borrowbox.com", request));

        assertTrue(ex.getMessage().contains("completed"));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("Review rejected if submitted by someone other than the renter")
    void testReviewNonRenterRejected() {
        Booking booking = Booking.builder()
                .id(100L)
                .equipment(equipment)
                .renter(renter)
                .owner(owner)
                .bookingStatus(BookingStatus.COMPLETED)
                .build();

        User thirdParty = User.builder().id(99L).email("stranger@borrowbox.com").build();

        ReviewDto.Request request = new ReviewDto.Request();
        request.setRating(4);

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("stranger@borrowbox.com")).thenReturn(Optional.of(thirdParty));

        assertThrows(UnauthorizedAccessException.class, () ->
                reviewService.createReview(100L, "stranger@borrowbox.com", request));
    }

    @Test
    @DisplayName("Toggle wishlist adds and removes equipment")
    void testToggleWishlist() {
        when(userRepository.findByEmail("alex.renter@borrowbox.com")).thenReturn(Optional.of(renter));
        when(equipmentRepository.findById(10L)).thenReturn(Optional.of(equipment));

        // 1. Not in wishlist -> adds it
        when(wishlistRepository.findByUserIdAndEquipmentId(2L, 10L)).thenReturn(Optional.empty());
        boolean added = wishlistService.toggleWishlist("alex.renter@borrowbox.com", 10L);
        assertTrue(added);
        verify(wishlistRepository).save(any(Wishlist.class));

        // 2. In wishlist -> removes it
        Wishlist existing = Wishlist.builder().id(1L).user(renter).equipment(equipment).build();
        when(wishlistRepository.findByUserIdAndEquipmentId(2L, 10L)).thenReturn(Optional.of(existing));
        boolean removed = wishlistService.toggleWishlist("alex.renter@borrowbox.com", 10L);
        assertFalse(removed);
        verify(wishlistRepository).delete(existing);
    }
}

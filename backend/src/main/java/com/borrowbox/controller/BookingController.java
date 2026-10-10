package com.borrowbox.controller;

import com.borrowbox.dto.booking.BookingDto;
import com.borrowbox.dto.booking.BookingPriceEstimate;
import com.borrowbox.dto.booking.BookingRequest;
import com.borrowbox.dto.common.ApiResponse;
import com.borrowbox.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/estimate")
    public ResponseEntity<ApiResponse<BookingPriceEstimate>> getPriceEstimate(
            @RequestParam Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BookingPriceEstimate estimate = bookingService.calculatePrice(equipmentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(estimate));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BookingRequest request) {
        BookingDto booking = bookingService.createBooking(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Booking request submitted successfully", booking));
    }

    @GetMapping("/my-rentals")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyRentals(@AuthenticationPrincipal UserDetails userDetails) {
        List<BookingDto> rentals = bookingService.getMyRentals(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(rentals));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyRequests(@AuthenticationPrincipal UserDetails userDetails) {
        List<BookingDto> requests = bookingService.getMyRequests(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingDto booking = bookingService.getBookingById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<BookingDto>> approveBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingDto approved = bookingService.approveBooking(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Booking approved successfully", approved));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<BookingDto>> rejectBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) ActionReasonRequest request) {
        String reason = request != null && request.getReason() != null ? request.getReason() : "Owner rejected booking request";
        BookingDto rejected = bookingService.rejectBooking(id, userDetails.getUsername(), reason);
        return ResponseEntity.ok(ApiResponse.success("Booking request rejected", rejected));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) ActionReasonRequest request) {
        String reason = request != null && request.getReason() != null ? request.getReason() : "Cancelled by user";
        BookingDto cancelled = bookingService.cancelBooking(id, userDetails.getUsername(), reason);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", cancelled));
    }

    @PostMapping("/{id}/confirm-pickup")
    public ResponseEntity<ApiResponse<BookingDto>> confirmPickup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingDto active = bookingService.confirmPickup(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Handover confirmed. Rental is now active.", active));
    }

    @PostMapping("/{id}/request-return")
    public ResponseEntity<ApiResponse<BookingDto>> requestReturn(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingDto updated = bookingService.requestReturn(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Return requested. Owner notified for inspection.", updated));
    }

    @PostMapping("/{id}/confirm-return")
    public ResponseEntity<ApiResponse<BookingDto>> confirmReturn(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        BookingDto completed = bookingService.confirmReturn(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Return confirmed. Rental completed!", completed));
    }

    public static class ActionReasonRequest {
        private String reason;

        public ActionReasonRequest() {}

        public ActionReasonRequest(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}

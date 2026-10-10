package com.borrowbox;

import com.borrowbox.dto.booking.BookingDto;
import com.borrowbox.dto.booking.BookingRequest;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.Booking;
import com.borrowbox.entity.BookingStatusHistory;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.BookingStatus;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.BookingStatusHistoryRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.service.AuthService;
import com.borrowbox.service.BookingService;
import com.borrowbox.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingLifecycleTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingStatusHistoryRepository historyRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BookingService bookingService;

    private User owner;
    private User renter;
    private Equipment equipment;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .fullName("Rahul Owner")
                .email("rahul.owner@borrowbox.com")
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
                .securityDeposit(new BigDecimal("5000.00"))
                .minimumRentalDays(1)
                .maximumRentalDays(14)
                .status(EquipmentStatus.AVAILABLE)
                .images(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Self-booking is prevented when owner attempts to book their own equipment")
    void testSelfBookingPrevented() {
        BookingRequest request = new BookingRequest();
        request.setEquipmentId(10L);
        request.setStartDate(LocalDate.of(2026, 11, 1));
        request.setEndDate(LocalDate.of(2026, 11, 3));

        when(userRepository.findByEmail("rahul.owner@borrowbox.com")).thenReturn(Optional.of(owner));
        when(equipmentRepository.findByIdWithLock(10L)).thenReturn(Optional.of(equipment));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                bookingService.createBooking("rahul.owner@borrowbox.com", request));

        assertTrue(ex.getMessage().contains("own equipment"));
    }

    @Test
    @DisplayName("Invalid date range is rejected when end date precedes start date")
    void testInvalidDatesRejected() {
        BookingRequest request = new BookingRequest();
        request.setEquipmentId(10L);
        request.setStartDate(LocalDate.of(2026, 11, 5));
        request.setEndDate(LocalDate.of(2026, 11, 2));

        when(userRepository.findByEmail("alex.renter@borrowbox.com")).thenReturn(Optional.of(renter));
        when(equipmentRepository.findByIdWithLock(10L)).thenReturn(Optional.of(equipment));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                bookingService.createBooking("alex.renter@borrowbox.com", request));

        assertTrue(ex.getMessage().contains("before start date"));
    }

    @Test
    @DisplayName("Complete booking lifecycle from pending to approved to active to return to completed")
    void testBookingLifecycleTransitions() {
        Booking booking = Booking.builder()
                .id(100L)
                .bookingReference("BB-2026-TEST")
                .equipment(equipment)
                .renter(renter)
                .owner(owner)
                .startDate(LocalDate.of(2026, 11, 1))
                .endDate(LocalDate.of(2026, 11, 3))
                .rentalDays(3)
                .dailyPriceSnapshot(new BigDecimal("2500.00"))
                .rentalSubtotal(new BigDecimal("7500.00"))
                .additionalFees(new BigDecimal("150.00"))
                .depositAmount(new BigDecimal("5000.00"))
                .totalAmount(new BigDecimal("12650.00"))
                .bookingStatus(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));
        when(authService.mapToUserDto(any())).thenReturn(UserDto.builder().id(1L).build());

        // 1. Owner approves
        BookingDto approvedDto = bookingService.approveBooking(100L, "rahul.owner@borrowbox.com");
        assertEquals(BookingStatus.APPROVED, approvedDto.getBookingStatus());

        // 2. Owner confirms pickup
        when(userRepository.findByEmail("rahul.owner@borrowbox.com")).thenReturn(Optional.of(owner));
        BookingDto activeDto = bookingService.confirmPickup(100L, "rahul.owner@borrowbox.com");
        assertEquals(BookingStatus.ACTIVE, activeDto.getBookingStatus());

        // 3. Renter requests return
        BookingDto returnReqDto = bookingService.requestReturn(100L, "alex.renter@borrowbox.com");
        assertEquals(BookingStatus.RETURN_REQUESTED, returnReqDto.getBookingStatus());

        // 4. Owner confirms return
        BookingDto completedDto = bookingService.confirmReturn(100L, "rahul.owner@borrowbox.com");
        assertEquals(BookingStatus.COMPLETED, completedDto.getBookingStatus());

        verify(notificationService, times(4)).createNotification(any(), any(), any(), any(), any());
        verify(historyRepository, times(4)).save(any(BookingStatusHistory.class));
    }

    @Test
    @DisplayName("Non-owner cannot approve booking request")
    void testNonOwnerCannotApproveBooking() {
        Booking booking = Booking.builder()
                .id(100L)
                .equipment(equipment)
                .renter(renter)
                .owner(owner)
                .bookingStatus(BookingStatus.PENDING)
                .build();

        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedAccessException.class, () ->
                bookingService.approveBooking(100L, "other.user@borrowbox.com"));
    }
}

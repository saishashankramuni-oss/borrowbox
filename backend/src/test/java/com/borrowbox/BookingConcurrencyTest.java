package com.borrowbox;

import com.borrowbox.dto.booking.BookingRequest;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.exception.BookingConflictException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingConcurrencyTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Reject booking request when dates overlap with existing confirmed booking")
    void testRejectOverlappingBooking() {
        User renter = User.builder().id(2L).email("renter@borrowbox.com").build();
        User owner = User.builder().id(1L).email("owner@borrowbox.com").build();

        Equipment equipment = Equipment.builder()
                .id(10L)
                .owner(owner)
                .name("Canon EOS R6")
                .dailyPrice(new BigDecimal("3200.00"))
                .securityDeposit(new BigDecimal("6000.00"))
                .minimumRentalDays(1)
                .maximumRentalDays(10)
                .status(EquipmentStatus.AVAILABLE)
                .build();

        when(userRepository.findByEmail("renter@borrowbox.com")).thenReturn(Optional.of(renter));
        when(equipmentRepository.findByIdWithLock(10L)).thenReturn(Optional.of(equipment));

        // Simulate existing conflicting booking found by server query
        when(bookingRepository.countConflictingBookings(eq(10L), any(), any(), any())).thenReturn(1L);

        BookingRequest request = new BookingRequest();
        request.setEquipmentId(10L);
        request.setStartDate(LocalDate.of(2026, 10, 15));
        request.setEndDate(LocalDate.of(2026, 10, 17));

        assertThrows(BookingConflictException.class, () -> {
            bookingService.createBooking("renter@borrowbox.com", request);
        });
    }
}

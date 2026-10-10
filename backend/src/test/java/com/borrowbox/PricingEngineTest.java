package com.borrowbox;

import com.borrowbox.dto.booking.BookingPriceEstimate;
import com.borrowbox.entity.Category;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.EquipmentRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingEngineTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("Calculate price accurately for 3 days rental with 2% service fee and deposit")
    void testCalculatePriceAccurate() {
        User owner = User.builder().id(1L).email("owner@borrowbox.com").build();
        Category category = Category.builder().id(1L).name("Cameras").build();

        Equipment equipment = Equipment.builder()
                .id(10L)
                .owner(owner)
                .category(category)
                .name("Sony A7 IV")
                .dailyPrice(new BigDecimal("2500.00"))
                .securityDeposit(new BigDecimal("5000.00"))
                .minimumRentalDays(1)
                .maximumRentalDays(30)
                .status(EquipmentStatus.AVAILABLE)
                .build();

        when(equipmentRepository.findById(10L)).thenReturn(Optional.of(equipment));
        when(bookingRepository.countConflictingBookings(eq(10L), any(), any(), any())).thenReturn(0L);

        LocalDate startDate = LocalDate.of(2026, 10, 15);
        LocalDate endDate = LocalDate.of(2026, 10, 17); // 3 days inclusive

        BookingPriceEstimate estimate = bookingService.calculatePrice(10L, startDate, endDate);

        assertNotNull(estimate);
        assertEquals(3, estimate.getRentalDays());
        assertEquals(new BigDecimal("2500.00"), estimate.getDailyPrice());
        assertEquals(new BigDecimal("7500.00"), estimate.getRentalSubtotal()); // 2500 * 3
        assertEquals(new BigDecimal("150.00"), estimate.getAdditionalFees()); // 7500 * 0.02
        assertEquals(new BigDecimal("5000.00"), estimate.getDepositAmount());
        assertEquals(new BigDecimal("12650.00"), estimate.getTotalAmount()); // 7500 + 150 + 5000
        assertTrue(estimate.isAvailable());
    }

    @Test
    @DisplayName("Mark equipment unavailable when status is not available even without overlap")
    void testCalculatePriceReportsUnavailableEquipment() {
        User owner = User.builder().id(1L).email("owner@borrowbox.com").build();
        Category category = Category.builder().id(1L).name("Cameras").build();

        Equipment equipment = Equipment.builder()
                .id(11L)
                .owner(owner)
                .category(category)
                .name("Sony A7S III")
                .dailyPrice(new BigDecimal("3000.00"))
                .securityDeposit(new BigDecimal("4500.00"))
                .minimumRentalDays(1)
                .maximumRentalDays(20)
                .status(EquipmentStatus.PAUSED)
                .build();

        when(equipmentRepository.findById(11L)).thenReturn(Optional.of(equipment));
        when(bookingRepository.countConflictingBookings(eq(11L), any(), any(), any())).thenReturn(0L);

        BookingPriceEstimate estimate = bookingService.calculatePrice(11L,
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 17));

        assertFalse(estimate.isAvailable());
        assertEquals("Equipment is currently unavailable for rent.", estimate.getAvailabilityMessage());
    }
}

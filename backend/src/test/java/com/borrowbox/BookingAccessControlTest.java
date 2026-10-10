package com.borrowbox;

import com.borrowbox.entity.Booking;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.AccountStatus;
import com.borrowbox.entity.enums.BookingStatus;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.BookingStatusHistoryRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.service.AuthService;
import com.borrowbox.service.BookingService;
import com.borrowbox.service.NotificationService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingAccessControlTest {

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

    @Test
    @DisplayName("Reject pickup confirmation from renter without owner/admin permissions")
    void testConfirmPickupRequiresOwnerOrAdmin() {
        User owner = User.builder()
                .id(1L)
                .fullName("Owner User")
                .email("owner@borrowbox.com")
                .role(Role.ROLE_OWNER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User renter = User.builder()
                .id(2L)
                .fullName("Renter User")
                .email("renter@borrowbox.com")
                .role(Role.ROLE_RENTER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

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

        Booking booking = Booking.builder()
                .id(99L)
                .bookingReference("BB-2026-0001")
                .equipment(equipment)
                .renter(renter)
                .owner(owner)
                .startDate(LocalDate.of(2026, 10, 15))
                .endDate(LocalDate.of(2026, 10, 17))
                .rentalDays(3)
                .dailyPriceSnapshot(new BigDecimal("3200.00"))
                .rentalSubtotal(new BigDecimal("9600.00"))
                .additionalFees(new BigDecimal("192.00"))
                .depositAmount(new BigDecimal("6000.00"))
                .totalAmount(new BigDecimal("15792.00"))
                .bookingStatus(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(99L)).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail("renter@borrowbox.com")).thenReturn(Optional.of(renter));

        assertThrows(UnauthorizedAccessException.class,
                () -> bookingService.confirmPickup(99L, "renter@borrowbox.com"));
    }
}

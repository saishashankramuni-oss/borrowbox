package com.borrowbox.service;

import com.borrowbox.dto.booking.BookingDto;
import com.borrowbox.dto.booking.BookingPriceEstimate;
import com.borrowbox.dto.booking.BookingRequest;
import com.borrowbox.entity.Booking;
import com.borrowbox.entity.BookingStatusHistory;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.BookingStatus;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.entity.enums.NotificationType;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.BadRequestException;
import com.borrowbox.exception.BookingConflictException;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.BookingRepository;
import com.borrowbox.repository.BookingStatusHistoryRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.dto.equipment.EquipmentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository historyRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuthService authService;

    public BookingService(BookingRepository bookingRepository,
                          BookingStatusHistoryRepository historyRepository,
                          EquipmentRepository equipmentRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          AuthService authService) {
        this.bookingRepository = bookingRepository;
        this.historyRepository = historyRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.authService = authService;
    }

    public static final List<BookingStatus> BLOCKING_STATUSES = List.of(
            BookingStatus.PENDING,
            BookingStatus.APPROVED,
            BookingStatus.READY_FOR_PICKUP,
            BookingStatus.ACTIVE,
            BookingStatus.RETURN_REQUESTED
    );

    @Transactional(readOnly = true)
    public BookingPriceEstimate calculatePrice(Long equipmentId, LocalDate startDate, LocalDate endDate) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + equipmentId));

        validateDates(startDate, endDate, equipment);

        int rentalDays = calculateRentalDays(startDate, endDate);
        BigDecimal subtotal = equipment.getDailyPrice().multiply(BigDecimal.valueOf(rentalDays));
        BigDecimal fees = subtotal.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP); // 2% service fee
        BigDecimal deposit = equipment.getSecurityDeposit() != null ? equipment.getSecurityDeposit() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(fees).add(deposit);

        boolean isConflicting = bookingRepository.countConflictingBookings(
                equipmentId, startDate, endDate, BLOCKING_STATUSES) > 0;
        boolean equipmentAvailable = equipment.getStatus() == EquipmentStatus.AVAILABLE && !isConflicting;
        String availabilityMessage = equipmentAvailable
                ? "Equipment is available for selected dates"
                : equipment.getStatus() != EquipmentStatus.AVAILABLE
                        ? "Equipment is currently unavailable for rent."
                        : "Equipment has existing overlapping bookings";

        return BookingPriceEstimate.builder()
                .equipmentId(equipment.getId())
                .equipmentName(equipment.getName())
                .startDate(startDate)
                .endDate(endDate)
                .rentalDays(rentalDays)
                .dailyPrice(equipment.getDailyPrice())
                .rentalSubtotal(subtotal)
                .additionalFees(fees)
                .depositAmount(deposit)
                .totalAmount(total)
                .available(equipmentAvailable)
                .availabilityMessage(availabilityMessage)
                .build();
    }

    @Transactional
    public BookingDto createBooking(String renterEmail, BookingRequest request) {
        User renter = userRepository.findByEmail(renterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // CRITICAL DOUBLE-BOOKING CONCURRENCY PROTECTION:
        // Lock equipment row with PESSIMISTIC_WRITE to prevent concurrent race condition requests
        Equipment equipment = equipmentRepository.findByIdWithLock(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + request.getEquipmentId()));

        if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
            throw new BadRequestException("This equipment is currently not available for rent.");
        }

        if (equipment.getOwner().getId().equals(renter.getId())) {
            throw new BadRequestException("You cannot rent your own equipment!");
        }

        validateDates(request.getStartDate(), request.getEndDate(), equipment);

        // Server-side check of overlapping blocking bookings
        long conflictCount = bookingRepository.countConflictingBookings(
                equipment.getId(), request.getStartDate(), request.getEndDate(), BLOCKING_STATUSES);

        if (conflictCount > 0) {
            throw new BookingConflictException("Selected dates overlap with an existing active or pending booking for this equipment.");
        }

        int rentalDays = calculateRentalDays(request.getStartDate(), request.getEndDate());
        BigDecimal dailyPriceSnapshot = equipment.getDailyPrice();
        BigDecimal subtotal = dailyPriceSnapshot.multiply(BigDecimal.valueOf(rentalDays));
        BigDecimal fees = subtotal.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deposit = equipment.getSecurityDeposit() != null ? equipment.getSecurityDeposit() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(fees).add(deposit);

        String reference = "BB-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = Booking.builder()
                .bookingReference(reference)
                .equipment(equipment)
                .renter(renter)
                .owner(equipment.getOwner())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .rentalDays(rentalDays)
                .dailyPriceSnapshot(dailyPriceSnapshot)
                .rentalSubtotal(subtotal)
                .additionalFees(fees)
                .depositAmount(deposit)
                .totalAmount(total)
                .bookingStatus(BookingStatus.PENDING)
                .paymentStatus("PAID") // Simulated payment successful
                .paymentTransactionId("TXN-" + System.currentTimeMillis())
                .build();

        booking = bookingRepository.save(booking);

        // Record history
        recordHistory(booking, null, BookingStatus.PENDING, renter, "Booking request created and payment simulated.");

        // Notify owner
        notificationService.createNotification(
                equipment.getOwner(),
                "New Booking Request for " + equipment.getName(),
                renter.getFullName() + " requested to rent " + equipment.getName() + " from " + request.getStartDate() + " to " + request.getEndDate(),
                NotificationType.BOOKING_REQUEST,
                booking.getId()
        );

        return mapToDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getMyRentals(String renterEmail) {
        User renter = userRepository.findByEmail(renterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByRenterIdOrderByCreatedAtDesc(renter.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getMyRequests(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isRenter = booking.getRenter().getId().equals(user.getId());
        boolean isOwner = booking.getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");

        if (!isRenter && !isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You do not have access to view this booking details.");
        }

        return mapToDto(booking);
    }

    @Transactional
    public BookingDto approveBooking(Long id, String ownerEmail) {
        Booking booking = getBookingForOwnerAction(id, ownerEmail);
        validateTransition(booking.getBookingStatus(), BookingStatus.APPROVED);

        booking.setBookingStatus(BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);

        recordHistory(booking, BookingStatus.PENDING, BookingStatus.APPROVED, booking.getOwner(), "Owner approved booking request.");

        notificationService.createNotification(
                booking.getRenter(),
                "Booking Approved!",
                booking.getOwner().getFullName() + " approved your rental request for " + booking.getEquipment().getName(),
                NotificationType.BOOKING_APPROVED,
                booking.getId()
        );

        return mapToDto(booking);
    }

    @Transactional
    public BookingDto rejectBooking(Long id, String ownerEmail, String reason) {
        Booking booking = getBookingForOwnerAction(id, ownerEmail);
        validateTransition(booking.getBookingStatus(), BookingStatus.REJECTED);

        BookingStatus prev = booking.getBookingStatus();
        booking.setBookingStatus(BookingStatus.REJECTED);
        booking.setCancellationReason(reason);
        booking = bookingRepository.save(booking);

        recordHistory(booking, prev, BookingStatus.REJECTED, booking.getOwner(), "Owner rejected request: " + reason);

        notificationService.createNotification(
                booking.getRenter(),
                "Booking Declined",
                "Your booking request for " + booking.getEquipment().getName() + " was declined. Refund processed.",
                NotificationType.BOOKING_REJECTED,
                booking.getId()
        );

        return mapToDto(booking);
    }

    @Transactional
    public BookingDto cancelBooking(Long id, String userEmail, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isRenter = booking.getRenter().getId().equals(user.getId());
        boolean isOwner = booking.getOwner().getId().equals(user.getId());

        if (!isRenter && !isOwner) {
            throw new UnauthorizedAccessException("Not authorized to cancel this booking!");
        }

        validateTransition(booking.getBookingStatus(), BookingStatus.CANCELLED);

        BookingStatus prev = booking.getBookingStatus();
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking = bookingRepository.save(booking);

        recordHistory(booking, prev, BookingStatus.CANCELLED, user, "Cancelled by " + user.getFullName() + ": " + reason);

        User notifyTarget = isRenter ? booking.getOwner() : booking.getRenter();
        notificationService.createNotification(
                notifyTarget,
                "Booking Cancelled",
                "Booking " + booking.getBookingReference() + " for " + booking.getEquipment().getName() + " was cancelled.",
                NotificationType.BOOKING_CANCELLED,
                booking.getId()
        );

        return mapToDto(booking);
    }

    @Transactional
    public BookingDto confirmPickup(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = booking.getOwner() != null && booking.getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && user.getRole() == Role.ROLE_ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("Only the equipment owner or an admin can confirm pickup.");
        }

        validateTransition(booking.getBookingStatus(), BookingStatus.ACTIVE);

        BookingStatus prev = booking.getBookingStatus();
        booking.setBookingStatus(BookingStatus.ACTIVE);
        booking = bookingRepository.save(booking);

        recordHistory(booking, prev, BookingStatus.ACTIVE, user, "Equipment handed over and pickup confirmed.");

        notificationService.createNotification(
                booking.getRenter(),
                "Rental Active",
                "Your rental for " + booking.getEquipment().getName() + " is now active. Enjoy!",
                NotificationType.RENTAL_ACTIVE,
                booking.getId()
        );

        return mapToDto(booking);
    }

    @Transactional
    public BookingDto requestReturn(Long id, String renterEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getRenter().getEmail().equalsIgnoreCase(renterEmail)) {
            throw new UnauthorizedAccessException("Only the renter can request item return!");
        }

        validateTransition(booking.getBookingStatus(), BookingStatus.RETURN_REQUESTED);

        BookingStatus prev = booking.getBookingStatus();
        booking.setBookingStatus(BookingStatus.RETURN_REQUESTED);
        booking = bookingRepository.save(booking);

        recordHistory(booking, prev, BookingStatus.RETURN_REQUESTED, booking.getRenter(), "Renter initiated return request.");

        notificationService.createNotification(
                booking.getOwner(),
                "Return Requested",
                booking.getRenter().getFullName() + " has returned " + booking.getEquipment().getName() + ". Please inspect and complete rental.",
                NotificationType.RETURN_REQUESTED,
                booking.getId()
        );

        return mapToDto(booking);
    }

    @Transactional
    public BookingDto confirmReturn(Long id, String ownerEmail) {
        Booking booking = getBookingForOwnerAction(id, ownerEmail);
        validateTransition(booking.getBookingStatus(), BookingStatus.COMPLETED);

        BookingStatus prev = booking.getBookingStatus();
        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);

        recordHistory(booking, prev, BookingStatus.COMPLETED, booking.getOwner(), "Owner confirmed return. Rental completed successfully!");

        notificationService.createNotification(
                booking.getRenter(),
                "Rental Completed",
                "Your rental for " + booking.getEquipment().getName() + " is complete. Deposit returned. Please leave a review!",
                NotificationType.RENTAL_COMPLETED,
                booking.getId()
        );

        return mapToDto(booking);
    }

    private Booking getBookingForOwnerAction(Long id, String ownerEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (!booking.getOwner().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new UnauthorizedAccessException("You are not the owner of this equipment!");
        }
        return booking;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate, Equipment equipment) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required.");
        }
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date.");
        }
        int days = calculateRentalDays(startDate, endDate);
        if (days < equipment.getMinimumRentalDays()) {
            throw new BadRequestException("Minimum rental duration for this equipment is " + equipment.getMinimumRentalDays() + " days.");
        }
        if (days > equipment.getMaximumRentalDays()) {
            throw new BadRequestException("Maximum rental duration for this equipment is " + equipment.getMaximumRentalDays() + " days.");
        }
    }

    private int calculateRentalDays(LocalDate start, LocalDate end) {
        // Inclusive calendar date rental: e.g. Oct 10 to Oct 12 = 3 days
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    private void validateTransition(BookingStatus current, BookingStatus target) {
        boolean valid = switch (target) {
            case APPROVED, REJECTED -> current == BookingStatus.PENDING;
            case READY_FOR_PICKUP -> current == BookingStatus.APPROVED;
            case ACTIVE -> current == BookingStatus.APPROVED || current == BookingStatus.READY_FOR_PICKUP;
            case RETURN_REQUESTED -> current == BookingStatus.ACTIVE;
            case COMPLETED -> current == BookingStatus.ACTIVE || current == BookingStatus.RETURN_REQUESTED;
            case CANCELLED -> current == BookingStatus.PENDING || current == BookingStatus.APPROVED;
            case DISPUTED -> current == BookingStatus.ACTIVE || current == BookingStatus.RETURN_REQUESTED;
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException("Invalid booking status transition from " + current + " to " + target);
        }
    }

    private void recordHistory(Booking booking, BookingStatus prev, BookingStatus next, User changedBy, String notes) {
        BookingStatusHistory history = BookingStatusHistory.builder()
                .booking(booking)
                .previousStatus(prev)
                .newStatus(next)
                .changedBy(changedBy)
                .notes(notes)
                .build();
        historyRepository.save(history);
    }

    public BookingDto mapToDto(Booking b) {
        List<BookingDto.BookingStatusHistoryDto> historyDtos = historyRepository
                .findByBookingIdOrderByCreatedAtAsc(b.getId())
                .stream()
                .map(h -> BookingDto.BookingStatusHistoryDto.builder()
                        .id(h.getId())
                        .previousStatus(h.getPreviousStatus())
                        .newStatus(h.getNewStatus())
                        .changedBy(authService.mapToUserDto(h.getChangedBy()))
                        .notes(h.getNotes())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return BookingDto.builder()
                .id(b.getId())
                .bookingReference(b.getBookingReference())
                .equipment(EquipmentDto.builder()
                        .id(b.getEquipment().getId())
                        .name(b.getEquipment().getName())
                        .location(b.getEquipment().getLocation())
                        .primaryImageUrl(!b.getEquipment().getImages().isEmpty() ? b.getEquipment().getImages().get(0).getFilePath() : "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80")
                        .dailyPrice(b.getEquipment().getDailyPrice())
                        .securityDeposit(b.getEquipment().getSecurityDeposit())
                        .build())
                .renter(authService.mapToUserDto(b.getRenter()))
                .owner(authService.mapToUserDto(b.getOwner()))
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .rentalDays(b.getRentalDays())
                .dailyPriceSnapshot(b.getDailyPriceSnapshot())
                .rentalSubtotal(b.getRentalSubtotal())
                .additionalFees(b.getAdditionalFees())
                .depositAmount(b.getDepositAmount())
                .totalAmount(b.getTotalAmount())
                .bookingStatus(b.getBookingStatus())
                .cancellationReason(b.getCancellationReason())
                .paymentStatus(b.getPaymentStatus())
                .paymentTransactionId(b.getPaymentTransactionId())
                .history(historyDtos)
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}

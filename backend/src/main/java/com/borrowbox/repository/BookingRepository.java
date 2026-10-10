package com.borrowbox.repository;

import com.borrowbox.entity.Booking;
import com.borrowbox.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByRenterIdOrderByCreatedAtDesc(Long renterId);

    List<Booking> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Booking> findByRenterIdAndBookingStatusOrderByCreatedAtDesc(Long renterId, BookingStatus status);

    List<Booking> findByOwnerIdAndBookingStatusOrderByCreatedAtDesc(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.equipment.id = :equipmentId " +
           "AND b.bookingStatus IN :blockingStatuses " +
           "AND (:startDate <= b.endDate AND :endDate >= b.startDate)")
    List<Booking> findConflictingBookings(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("blockingStatuses") List<BookingStatus> blockingStatuses
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.equipment.id = :equipmentId " +
           "AND b.bookingStatus IN :blockingStatuses " +
           "AND (:startDate <= b.endDate AND :endDate >= b.startDate)")
    long countConflictingBookings(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("blockingStatuses") List<BookingStatus> blockingStatuses
    );

    long countByOwnerId(Long ownerId);

    long countByOwnerIdAndBookingStatus(Long ownerId, BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.rentalSubtotal), 0) FROM Booking b WHERE b.owner.id = :ownerId AND b.bookingStatus = 'COMPLETED'")
    Double calculateTotalOwnerEarnings(@Param("ownerId") Long ownerId);

    boolean existsByEquipmentIdAndRenterIdAndBookingStatus(Long equipmentId, Long renterId, BookingStatus status);
}

package com.borrowbox.entity;

import com.borrowbox.entity.enums.BookingStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true, length = 36)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "renter_id", nullable = false)
    private User renter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "rental_days", nullable = false)
    private Integer rentalDays;

    @Column(name = "daily_price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyPriceSnapshot;

    @Column(name = "rental_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalSubtotal;

    @Column(name = "additional_fees", precision = 10, scale = 2)
    private BigDecimal additionalFees;

    @Column(name = "deposit_amount", precision = 10, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 30)
    private BookingStatus bookingStatus;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Booking() {}

    public Booking(Long id, String bookingReference, Equipment equipment, User renter, User owner, LocalDate startDate, LocalDate endDate, Integer rentalDays, BigDecimal dailyPriceSnapshot, BigDecimal rentalSubtotal, BigDecimal additionalFees, BigDecimal depositAmount, BigDecimal totalAmount, BookingStatus bookingStatus, String cancellationReason, String paymentStatus, String paymentTransactionId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.equipment = equipment;
        this.renter = renter;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalDays = rentalDays;
        this.dailyPriceSnapshot = dailyPriceSnapshot;
        this.rentalSubtotal = rentalSubtotal;
        this.additionalFees = additionalFees;
        this.depositAmount = depositAmount;
        this.totalAmount = totalAmount;
        this.bookingStatus = bookingStatus;
        this.cancellationReason = cancellationReason;
        this.paymentStatus = paymentStatus;
        this.paymentTransactionId = paymentTransactionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String bookingReference;
        private Equipment equipment;
        private User renter;
        private User owner;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer rentalDays;
        private BigDecimal dailyPriceSnapshot;
        private BigDecimal rentalSubtotal;
        private BigDecimal additionalFees = BigDecimal.ZERO;
        private BigDecimal depositAmount = BigDecimal.ZERO;
        private BigDecimal totalAmount;
        private BookingStatus bookingStatus = BookingStatus.PENDING;
        private String cancellationReason;
        private String paymentStatus = "PAID";
        private String paymentTransactionId;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public Builder equipment(Equipment equipment) { this.equipment = equipment; return this; }
        public Builder renter(User renter) { this.renter = renter; return this; }
        public Builder owner(User owner) { this.owner = owner; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder rentalDays(Integer rentalDays) { this.rentalDays = rentalDays; return this; }
        public Builder dailyPriceSnapshot(BigDecimal dailyPriceSnapshot) { this.dailyPriceSnapshot = dailyPriceSnapshot; return this; }
        public Builder rentalSubtotal(BigDecimal rentalSubtotal) { this.rentalSubtotal = rentalSubtotal; return this; }
        public Builder additionalFees(BigDecimal additionalFees) { this.additionalFees = additionalFees; return this; }
        public Builder depositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder bookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; return this; }
        public Builder cancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; return this; }
        public Builder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder paymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; return this; }

        public Booking build() {
            return new Booking(id, bookingReference, equipment, renter, owner, startDate, endDate, rentalDays, dailyPriceSnapshot, rentalSubtotal, additionalFees, depositAmount, totalAmount, bookingStatus, cancellationReason, paymentStatus, paymentTransactionId, null, null);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public User getRenter() { return renter; }
    public void setRenter(User renter) { this.renter = renter; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }

    public BigDecimal getDailyPriceSnapshot() { return dailyPriceSnapshot; }
    public void setDailyPriceSnapshot(BigDecimal dailyPriceSnapshot) { this.dailyPriceSnapshot = dailyPriceSnapshot; }

    public BigDecimal getRentalSubtotal() { return rentalSubtotal; }
    public void setRentalSubtotal(BigDecimal rentalSubtotal) { this.rentalSubtotal = rentalSubtotal; }

    public BigDecimal getAdditionalFees() { return additionalFees; }
    public void setAdditionalFees(BigDecimal additionalFees) { this.additionalFees = additionalFees; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

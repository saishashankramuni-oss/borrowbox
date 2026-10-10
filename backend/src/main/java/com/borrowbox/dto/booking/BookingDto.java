package com.borrowbox.dto.booking;

import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingDto {
    private Long id;
    private String bookingReference;
    private EquipmentDto equipment;
    private UserDto renter;
    private UserDto owner;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer rentalDays;
    private BigDecimal dailyPriceSnapshot;
    private BigDecimal rentalSubtotal;
    private BigDecimal additionalFees;
    private BigDecimal depositAmount;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private String cancellationReason;
    private String paymentStatus;
    private String paymentTransactionId;
    private List<BookingStatusHistoryDto> history;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingDto() {}

    public BookingDto(Long id, String bookingReference, EquipmentDto equipment, UserDto renter, UserDto owner, LocalDate startDate, LocalDate endDate, Integer rentalDays, BigDecimal dailyPriceSnapshot, BigDecimal rentalSubtotal, BigDecimal additionalFees, BigDecimal depositAmount, BigDecimal totalAmount, BookingStatus bookingStatus, String cancellationReason, String paymentStatus, String paymentTransactionId, List<BookingStatusHistoryDto> history, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
        this.history = history;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String bookingReference;
        private EquipmentDto equipment;
        private UserDto renter;
        private UserDto owner;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer rentalDays;
        private BigDecimal dailyPriceSnapshot;
        private BigDecimal rentalSubtotal;
        private BigDecimal additionalFees;
        private BigDecimal depositAmount;
        private BigDecimal totalAmount;
        private BookingStatus bookingStatus;
        private String cancellationReason;
        private String paymentStatus;
        private String paymentTransactionId;
        private List<BookingStatusHistoryDto> history;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder bookingReference(String bookingReference) { this.bookingReference = bookingReference; return this; }
        public Builder equipment(EquipmentDto equipment) { this.equipment = equipment; return this; }
        public Builder renter(UserDto renter) { this.renter = renter; return this; }
        public Builder owner(UserDto owner) { this.owner = owner; return this; }
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
        public Builder history(List<BookingStatusHistoryDto> history) { this.history = history; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public BookingDto build() {
            return new BookingDto(id, bookingReference, equipment, renter, owner, startDate, endDate, rentalDays, dailyPriceSnapshot, rentalSubtotal, additionalFees, depositAmount, totalAmount, bookingStatus, cancellationReason, paymentStatus, paymentTransactionId, history, createdAt, updatedAt);
        }
    }

    public static class BookingStatusHistoryDto {
        private Long id;
        private BookingStatus previousStatus;
        private BookingStatus newStatus;
        private UserDto changedBy;
        private String notes;
        private LocalDateTime createdAt;

        public BookingStatusHistoryDto() {}

        public BookingStatusHistoryDto(Long id, BookingStatus previousStatus, BookingStatus newStatus, UserDto changedBy, String notes, LocalDateTime createdAt) {
            this.id = id;
            this.previousStatus = previousStatus;
            this.newStatus = newStatus;
            this.changedBy = changedBy;
            this.notes = notes;
            this.createdAt = createdAt;
        }

        public static HistoryBuilder builder() { return new HistoryBuilder(); }

        public static class HistoryBuilder {
            private Long id;
            private BookingStatus previousStatus;
            private BookingStatus newStatus;
            private UserDto changedBy;
            private String notes;
            private LocalDateTime createdAt;

            public HistoryBuilder id(Long id) { this.id = id; return this; }
            public HistoryBuilder previousStatus(BookingStatus previousStatus) { this.previousStatus = previousStatus; return this; }
            public HistoryBuilder newStatus(BookingStatus newStatus) { this.newStatus = newStatus; return this; }
            public HistoryBuilder changedBy(UserDto changedBy) { this.changedBy = changedBy; return this; }
            public HistoryBuilder notes(String notes) { this.notes = notes; return this; }
            public HistoryBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

            public BookingStatusHistoryDto build() {
                return new BookingStatusHistoryDto(id, previousStatus, newStatus, changedBy, notes, createdAt);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public BookingStatus getPreviousStatus() { return previousStatus; }
        public void setPreviousStatus(BookingStatus previousStatus) { this.previousStatus = previousStatus; }

        public BookingStatus getNewStatus() { return newStatus; }
        public void setNewStatus(BookingStatus newStatus) { this.newStatus = newStatus; }

        public UserDto getChangedBy() { return changedBy; }
        public void setChangedBy(UserDto changedBy) { this.changedBy = changedBy; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public EquipmentDto getEquipment() { return equipment; }
    public void setEquipment(EquipmentDto equipment) { this.equipment = equipment; }

    public UserDto getRenter() { return renter; }
    public void setRenter(UserDto renter) { this.renter = renter; }

    public UserDto getOwner() { return owner; }
    public void setOwner(UserDto owner) { this.owner = owner; }

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

    public List<BookingStatusHistoryDto> getHistory() { return history; }
    public void setHistory(List<BookingStatusHistoryDto> history) { this.history = history; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

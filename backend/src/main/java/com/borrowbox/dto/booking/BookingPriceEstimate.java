package com.borrowbox.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingPriceEstimate {
    private Long equipmentId;
    private String equipmentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int rentalDays;
    private BigDecimal dailyPrice;
    private BigDecimal rentalSubtotal;
    private BigDecimal additionalFees;
    private BigDecimal depositAmount;
    private BigDecimal totalAmount;
    private boolean available;
    private String availabilityMessage;

    public BookingPriceEstimate() {}

    public BookingPriceEstimate(Long equipmentId, String equipmentName, LocalDate startDate, LocalDate endDate, int rentalDays, BigDecimal dailyPrice, BigDecimal rentalSubtotal, BigDecimal additionalFees, BigDecimal depositAmount, BigDecimal totalAmount, boolean available, String availabilityMessage) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalDays = rentalDays;
        this.dailyPrice = dailyPrice;
        this.rentalSubtotal = rentalSubtotal;
        this.additionalFees = additionalFees;
        this.depositAmount = depositAmount;
        this.totalAmount = totalAmount;
        this.available = available;
        this.availabilityMessage = availabilityMessage;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long equipmentId;
        private String equipmentName;
        private LocalDate startDate;
        private LocalDate endDate;
        private int rentalDays;
        private BigDecimal dailyPrice;
        private BigDecimal rentalSubtotal;
        private BigDecimal additionalFees;
        private BigDecimal depositAmount;
        private BigDecimal totalAmount;
        private boolean available;
        private String availabilityMessage;

        public Builder equipmentId(Long equipmentId) { this.equipmentId = equipmentId; return this; }
        public Builder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder rentalDays(int rentalDays) { this.rentalDays = rentalDays; return this; }
        public Builder dailyPrice(BigDecimal dailyPrice) { this.dailyPrice = dailyPrice; return this; }
        public Builder rentalSubtotal(BigDecimal rentalSubtotal) { this.rentalSubtotal = rentalSubtotal; return this; }
        public Builder additionalFees(BigDecimal additionalFees) { this.additionalFees = additionalFees; return this; }
        public Builder depositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder available(boolean available) { this.available = available; return this; }
        public Builder availabilityMessage(String availabilityMessage) { this.availabilityMessage = availabilityMessage; return this; }

        public BookingPriceEstimate build() {
            return new BookingPriceEstimate(equipmentId, equipmentName, startDate, endDate, rentalDays, dailyPrice, rentalSubtotal, additionalFees, depositAmount, totalAmount, available, availabilityMessage);
        }
    }

    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getRentalDays() { return rentalDays; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }

    public BigDecimal getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(BigDecimal dailyPrice) { this.dailyPrice = dailyPrice; }

    public BigDecimal getRentalSubtotal() { return rentalSubtotal; }
    public void setRentalSubtotal(BigDecimal rentalSubtotal) { this.rentalSubtotal = rentalSubtotal; }

    public BigDecimal getAdditionalFees() { return additionalFees; }
    public void setAdditionalFees(BigDecimal additionalFees) { this.additionalFees = additionalFees; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getAvailabilityMessage() { return availabilityMessage; }
    public void setAvailabilityMessage(String availabilityMessage) { this.availabilityMessage = availabilityMessage; }
}

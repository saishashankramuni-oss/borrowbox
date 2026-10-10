package com.borrowbox.dto.admin;

import java.math.BigDecimal;

public class AdminOverviewDto {
    private long totalUsers;
    private long totalOwners;
    private long totalRenters;
    private long totalEquipment;
    private long activeEquipment;
    private long totalBookings;
    private long activeBookings;
    private long openDisputes;
    private BigDecimal totalGrossVolume;

    public AdminOverviewDto() {}

    public AdminOverviewDto(long totalUsers, long totalOwners, long totalRenters, long totalEquipment, long activeEquipment, long totalBookings, long activeBookings, long openDisputes, BigDecimal totalGrossVolume) {
        this.totalUsers = totalUsers;
        this.totalOwners = totalOwners;
        this.totalRenters = totalRenters;
        this.totalEquipment = totalEquipment;
        this.activeEquipment = activeEquipment;
        this.totalBookings = totalBookings;
        this.activeBookings = activeBookings;
        this.openDisputes = openDisputes;
        this.totalGrossVolume = totalGrossVolume;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalUsers;
        private long totalOwners;
        private long totalRenters;
        private long totalEquipment;
        private long activeEquipment;
        private long totalBookings;
        private long activeBookings;
        private long openDisputes;
        private BigDecimal totalGrossVolume;

        public Builder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public Builder totalOwners(long totalOwners) { this.totalOwners = totalOwners; return this; }
        public Builder totalRenters(long totalRenters) { this.totalRenters = totalRenters; return this; }
        public Builder totalEquipment(long totalEquipment) { this.totalEquipment = totalEquipment; return this; }
        public Builder activeEquipment(long activeEquipment) { this.activeEquipment = activeEquipment; return this; }
        public Builder totalBookings(long totalBookings) { this.totalBookings = totalBookings; return this; }
        public Builder activeBookings(long activeBookings) { this.activeBookings = activeBookings; return this; }
        public Builder openDisputes(long openDisputes) { this.openDisputes = openDisputes; return this; }
        public Builder totalGrossVolume(BigDecimal totalGrossVolume) { this.totalGrossVolume = totalGrossVolume; return this; }

        public AdminOverviewDto build() {
            return new AdminOverviewDto(totalUsers, totalOwners, totalRenters, totalEquipment, activeEquipment, totalBookings, activeBookings, openDisputes, totalGrossVolume);
        }
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalOwners() { return totalOwners; }
    public void setTotalOwners(long totalOwners) { this.totalOwners = totalOwners; }

    public long getTotalRenters() { return totalRenters; }
    public void setTotalRenters(long totalRenters) { this.totalRenters = totalRenters; }

    public long getTotalEquipment() { return totalEquipment; }
    public void setTotalEquipment(long totalEquipment) { this.totalEquipment = totalEquipment; }

    public long getActiveEquipment() { return activeEquipment; }
    public void setActiveEquipment(long activeEquipment) { this.activeEquipment = activeEquipment; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getActiveBookings() { return activeBookings; }
    public void setActiveBookings(long activeBookings) { this.activeBookings = activeBookings; }

    public long getOpenDisputes() { return openDisputes; }
    public void setOpenDisputes(long openDisputes) { this.openDisputes = openDisputes; }

    public BigDecimal getTotalGrossVolume() { return totalGrossVolume; }
    public void setTotalGrossVolume(BigDecimal totalGrossVolume) { this.totalGrossVolume = totalGrossVolume; }
}

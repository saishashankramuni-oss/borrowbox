package com.borrowbox.entity.enums;

public enum BookingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    READY_FOR_PICKUP,
    ACTIVE,
    RETURN_REQUESTED,
    COMPLETED,
    DISPUTED,
    EXPIRED;

    public boolean blocksAvailability() {
        return this == PENDING || 
               this == APPROVED || 
               this == READY_FOR_PICKUP || 
               this == ACTIVE || 
               this == RETURN_REQUESTED;
    }
}

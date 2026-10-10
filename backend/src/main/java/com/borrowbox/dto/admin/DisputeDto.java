package com.borrowbox.dto.admin;

import com.borrowbox.dto.booking.BookingDto;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.enums.DisputeStatus;

import java.time.LocalDateTime;

public class DisputeDto {
    private Long id;
    private BookingDto booking;
    private UserDto raisedBy;
    private String reason;
    private String description;
    private DisputeStatus status;
    private String resolutionNotes;
    private LocalDateTime createdAt;

    public DisputeDto() {}

    public DisputeDto(Long id, BookingDto booking, UserDto raisedBy, String reason, String description, DisputeStatus status, String resolutionNotes, LocalDateTime createdAt) {
        this.id = id;
        this.booking = booking;
        this.raisedBy = raisedBy;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.resolutionNotes = resolutionNotes;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private BookingDto booking;
        private UserDto raisedBy;
        private String reason;
        private String description;
        private DisputeStatus status;
        private String resolutionNotes;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder booking(BookingDto booking) { this.booking = booking; return this; }
        public Builder raisedBy(UserDto raisedBy) { this.raisedBy = raisedBy; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(DisputeStatus status) { this.status = status; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DisputeDto build() {
            return new DisputeDto(id, booking, raisedBy, reason, description, status, resolutionNotes, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BookingDto getBooking() { return booking; }
    public void setBooking(BookingDto booking) { this.booking = booking; }

    public UserDto getRaisedBy() { return raisedBy; }
    public void setRaisedBy(UserDto raisedBy) { this.raisedBy = raisedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DisputeStatus getStatus() { return status; }
    public void setStatus(DisputeStatus status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

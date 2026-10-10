package com.borrowbox.entity;

import com.borrowbox.entity.enums.BookingStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_status_history")
public class BookingStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private BookingStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private BookingStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private User changedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BookingStatusHistory() {}

    public BookingStatusHistory(Long id, Booking booking, BookingStatus previousStatus, BookingStatus newStatus, User changedBy, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.booking = booking;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Booking booking;
        private BookingStatus previousStatus;
        private BookingStatus newStatus;
        private User changedBy;
        private String notes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder booking(Booking booking) { this.booking = booking; return this; }
        public Builder previousStatus(BookingStatus previousStatus) { this.previousStatus = previousStatus; return this; }
        public Builder newStatus(BookingStatus newStatus) { this.newStatus = newStatus; return this; }
        public Builder changedBy(User changedBy) { this.changedBy = changedBy; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public BookingStatusHistory build() {
            return new BookingStatusHistory(id, booking, previousStatus, newStatus, changedBy, notes, null);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public BookingStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(BookingStatus previousStatus) { this.previousStatus = previousStatus; }

    public BookingStatus getNewStatus() { return newStatus; }
    public void setNewStatus(BookingStatus newStatus) { this.newStatus = newStatus; }

    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

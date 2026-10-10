package com.borrowbox.entity;

import com.borrowbox.entity.enums.InspectionStage;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "condition_reports")
public class ConditionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_stage", nullable = false, length = 30)
    private InspectionStage inspectionStage;

    @Column(name = "condition_notes", columnDefinition = "TEXT", nullable = false)
    private String conditionNotes;

    @Column(name = "damage_notes", columnDefinition = "TEXT")
    private String damageNotes;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "conditionReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConditionReportImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ConditionReport() {}

    public ConditionReport(Long id, Booking booking, User reporter, InspectionStage inspectionStage, String conditionNotes, String damageNotes, LocalDateTime confirmedAt, List<ConditionReportImage> images, LocalDateTime createdAt) {
        this.id = id;
        this.booking = booking;
        this.reporter = reporter;
        this.inspectionStage = inspectionStage;
        this.conditionNotes = conditionNotes;
        this.damageNotes = damageNotes;
        this.confirmedAt = confirmedAt;
        this.images = images != null ? images : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Booking booking;
        private User reporter;
        private InspectionStage inspectionStage;
        private String conditionNotes;
        private String damageNotes;
        private LocalDateTime confirmedAt;
        private List<ConditionReportImage> images = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder booking(Booking booking) { this.booking = booking; return this; }
        public Builder reporter(User reporter) { this.reporter = reporter; return this; }
        public Builder inspectionStage(InspectionStage inspectionStage) { this.inspectionStage = inspectionStage; return this; }
        public Builder conditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; return this; }
        public Builder damageNotes(String damageNotes) { this.damageNotes = damageNotes; return this; }
        public Builder confirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; return this; }
        public Builder images(List<ConditionReportImage> images) { this.images = images; return this; }

        public ConditionReport build() {
            return new ConditionReport(id, booking, reporter, inspectionStage, conditionNotes, damageNotes, confirmedAt, images, null);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public User getReporter() { return reporter; }
    public void setReporter(User reporter) { this.reporter = reporter; }

    public InspectionStage getInspectionStage() { return inspectionStage; }
    public void setInspectionStage(InspectionStage inspectionStage) { this.inspectionStage = inspectionStage; }

    public String getConditionNotes() { return conditionNotes; }
    public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }

    public String getDamageNotes() { return damageNotes; }
    public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public List<ConditionReportImage> getImages() { return images; }
    public void setImages(List<ConditionReportImage> images) { this.images = images; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

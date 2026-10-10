package com.borrowbox.dto.condition;

import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.enums.InspectionStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class ConditionReportDto {
    private Long id;
    private Long bookingId;
    private UserDto reporter;
    private InspectionStage inspectionStage;
    private String conditionNotes;
    private String damageNotes;
    private List<String> imagePaths;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;

    public ConditionReportDto() {}

    public ConditionReportDto(Long id, Long bookingId, UserDto reporter, InspectionStage inspectionStage, String conditionNotes, String damageNotes, List<String> imagePaths, LocalDateTime confirmedAt, LocalDateTime createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.reporter = reporter;
        this.inspectionStage = inspectionStage;
        this.conditionNotes = conditionNotes;
        this.damageNotes = damageNotes;
        this.imagePaths = imagePaths;
        this.confirmedAt = confirmedAt;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long bookingId;
        private UserDto reporter;
        private InspectionStage inspectionStage;
        private String conditionNotes;
        private String damageNotes;
        private List<String> imagePaths;
        private LocalDateTime confirmedAt;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public Builder reporter(UserDto reporter) { this.reporter = reporter; return this; }
        public Builder inspectionStage(InspectionStage inspectionStage) { this.inspectionStage = inspectionStage; return this; }
        public Builder conditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; return this; }
        public Builder damageNotes(String damageNotes) { this.damageNotes = damageNotes; return this; }
        public Builder imagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; return this; }
        public Builder confirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ConditionReportDto build() {
            return new ConditionReportDto(id, bookingId, reporter, inspectionStage, conditionNotes, damageNotes, imagePaths, confirmedAt, createdAt);
        }
    }

    public static class Request {
        @NotNull(message = "Inspection stage is required")
        private InspectionStage inspectionStage;

        @NotBlank(message = "Condition notes are required")
        private String conditionNotes;

        private String damageNotes;
        private List<String> imagePaths;

        public InspectionStage getInspectionStage() { return inspectionStage; }
        public void setInspectionStage(InspectionStage inspectionStage) { this.inspectionStage = inspectionStage; }

        public String getConditionNotes() { return conditionNotes; }
        public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }

        public String getDamageNotes() { return damageNotes; }
        public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }

        public List<String> getImagePaths() { return imagePaths; }
        public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public UserDto getReporter() { return reporter; }
    public void setReporter(UserDto reporter) { this.reporter = reporter; }

    public InspectionStage getInspectionStage() { return inspectionStage; }
    public void setInspectionStage(InspectionStage inspectionStage) { this.inspectionStage = inspectionStage; }

    public String getConditionNotes() { return conditionNotes; }
    public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }

    public String getDamageNotes() { return damageNotes; }
    public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

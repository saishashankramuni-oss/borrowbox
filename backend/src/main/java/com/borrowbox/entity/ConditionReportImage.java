package com.borrowbox.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "condition_report_images")
public class ConditionReportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "condition_report_id", nullable = false)
    private ConditionReport conditionReport;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ConditionReportImage() {}

    public ConditionReportImage(Long id, ConditionReport conditionReport, String filePath, LocalDateTime createdAt) {
        this.id = id;
        this.conditionReport = conditionReport;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private ConditionReport conditionReport;
        private String filePath;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder conditionReport(ConditionReport conditionReport) { this.conditionReport = conditionReport; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }

        public ConditionReportImage build() {
            return new ConditionReportImage(id, conditionReport, filePath, null);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ConditionReport getConditionReport() { return conditionReport; }
    public void setConditionReport(ConditionReport conditionReport) { this.conditionReport = conditionReport; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

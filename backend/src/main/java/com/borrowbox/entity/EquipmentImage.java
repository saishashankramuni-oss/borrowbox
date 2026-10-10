package com.borrowbox.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_images")
public class EquipmentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "alt_text", length = 150)
    private String altText;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public EquipmentImage() {}

    public EquipmentImage(Long id, Equipment equipment, String filePath, String altText, Integer displayOrder, Boolean isPrimary, LocalDateTime createdAt) {
        this.id = id;
        this.equipment = equipment;
        this.filePath = filePath;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Equipment equipment;
        private String filePath;
        private String altText;
        private Integer displayOrder = 0;
        private Boolean isPrimary = false;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder equipment(Equipment equipment) { this.equipment = equipment; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder altText(String altText) { this.altText = altText; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public Builder isPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; return this; }

        public EquipmentImage build() {
            return new EquipmentImage(id, equipment, filePath, altText, displayOrder, isPrimary, null);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

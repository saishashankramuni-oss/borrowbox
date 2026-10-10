package com.borrowbox.entity;

import com.borrowbox.entity.enums.EquipmentCondition;
import com.borrowbox.entity.enums.EquipmentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false, length = 30)
    private EquipmentCondition itemCondition;

    @Column(name = "daily_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyPrice;

    @Column(name = "security_deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal securityDeposit;

    @Column(name = "minimum_rental_days", nullable = false)
    private Integer minimumRentalDays;

    @Column(name = "maximum_rental_days", nullable = false)
    private Integer maximumRentalDays;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(name = "rental_instructions", columnDefinition = "TEXT")
    private String rentalInstructions;

    @Column(name = "rental_terms", columnDefinition = "TEXT")
    private String rentalTerms;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EquipmentStatus status;

    @Column(name = "rating_average", precision = 3, scale = 2)
    private BigDecimal ratingAverage;

    @Column(name = "review_count")
    private Integer reviewCount;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipmentImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Equipment() {}

    public Equipment(Long id, User owner, Category category, String name, String slug, String description, String brand, String model, EquipmentCondition itemCondition, BigDecimal dailyPrice, BigDecimal securityDeposit, Integer minimumRentalDays, Integer maximumRentalDays, String location, String rentalInstructions, String rentalTerms, EquipmentStatus status, BigDecimal ratingAverage, Integer reviewCount, List<EquipmentImage> images, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.category = category;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.brand = brand;
        this.model = model;
        this.itemCondition = itemCondition;
        this.dailyPrice = dailyPrice;
        this.securityDeposit = securityDeposit;
        this.minimumRentalDays = minimumRentalDays;
        this.maximumRentalDays = maximumRentalDays;
        this.location = location;
        this.rentalInstructions = rentalInstructions;
        this.rentalTerms = rentalTerms;
        this.status = status;
        this.ratingAverage = ratingAverage;
        this.reviewCount = reviewCount;
        this.images = images != null ? images : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User owner;
        private Category category;
        private String name;
        private String slug;
        private String description;
        private String brand;
        private String model;
        private EquipmentCondition itemCondition;
        private BigDecimal dailyPrice;
        private BigDecimal securityDeposit;
        private Integer minimumRentalDays = 1;
        private Integer maximumRentalDays = 30;
        private String location;
        private String rentalInstructions;
        private String rentalTerms;
        private EquipmentStatus status = EquipmentStatus.AVAILABLE;
        private BigDecimal ratingAverage = BigDecimal.ZERO;
        private Integer reviewCount = 0;
        private List<EquipmentImage> images = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder owner(User owner) { this.owner = owner; return this; }
        public Builder category(Category category) { this.category = category; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder itemCondition(EquipmentCondition itemCondition) { this.itemCondition = itemCondition; return this; }
        public Builder dailyPrice(BigDecimal dailyPrice) { this.dailyPrice = dailyPrice; return this; }
        public Builder securityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; return this; }
        public Builder minimumRentalDays(Integer minimumRentalDays) { this.minimumRentalDays = minimumRentalDays; return this; }
        public Builder maximumRentalDays(Integer maximumRentalDays) { this.maximumRentalDays = maximumRentalDays; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder rentalInstructions(String rentalInstructions) { this.rentalInstructions = rentalInstructions; return this; }
        public Builder rentalTerms(String rentalTerms) { this.rentalTerms = rentalTerms; return this; }
        public Builder status(EquipmentStatus status) { this.status = status; return this; }
        public Builder ratingAverage(BigDecimal ratingAverage) { this.ratingAverage = ratingAverage; return this; }
        public Builder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public Builder images(List<EquipmentImage> images) { this.images = images; return this; }

        public Equipment build() {
            return new Equipment(id, owner, category, name, slug, description, brand, model, itemCondition, dailyPrice, securityDeposit, minimumRentalDays, maximumRentalDays, location, rentalInstructions, rentalTerms, status, ratingAverage, reviewCount, images, null, null);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public EquipmentCondition getItemCondition() { return itemCondition; }
    public void setItemCondition(EquipmentCondition itemCondition) { this.itemCondition = itemCondition; }

    public BigDecimal getDailyPrice() { return dailyPrice; }
    public void setDailyPrice(BigDecimal dailyPrice) { this.dailyPrice = dailyPrice; }

    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }

    public Integer getMinimumRentalDays() { return minimumRentalDays; }
    public void setMinimumRentalDays(Integer minimumRentalDays) { this.minimumRentalDays = minimumRentalDays; }

    public Integer getMaximumRentalDays() { return maximumRentalDays; }
    public void setMaximumRentalDays(Integer maximumRentalDays) { this.maximumRentalDays = maximumRentalDays; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRentalInstructions() { return rentalInstructions; }
    public void setRentalInstructions(String rentalInstructions) { this.rentalInstructions = rentalInstructions; }

    public String getRentalTerms() { return rentalTerms; }
    public void setRentalTerms(String rentalTerms) { this.rentalTerms = rentalTerms; }

    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }

    public BigDecimal getRatingAverage() { return ratingAverage; }
    public void setRatingAverage(BigDecimal ratingAverage) { this.ratingAverage = ratingAverage; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public List<EquipmentImage> getImages() { return images; }
    public void setImages(List<EquipmentImage> images) { this.images = images; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

package com.borrowbox.dto.equipment;

import com.borrowbox.dto.category.CategoryDto;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.enums.EquipmentCondition;
import com.borrowbox.entity.enums.EquipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EquipmentDto {
    private Long id;
    private UserDto owner;
    private CategoryDto category;
    private String name;
    private String slug;
    private String description;
    private String brand;
    private String model;
    private EquipmentCondition itemCondition;
    private BigDecimal dailyPrice;
    private BigDecimal securityDeposit;
    private Integer minimumRentalDays;
    private Integer maximumRentalDays;
    private String location;
    private String rentalInstructions;
    private String rentalTerms;
    private EquipmentStatus status;
    private BigDecimal ratingAverage;
    private Integer reviewCount;
    private List<EquipmentImageDto> images;
    private String primaryImageUrl;
    private LocalDateTime createdAt;

    public EquipmentDto() {}

    public EquipmentDto(Long id, UserDto owner, CategoryDto category, String name, String slug, String description, String brand, String model, EquipmentCondition itemCondition, BigDecimal dailyPrice, BigDecimal securityDeposit, Integer minimumRentalDays, Integer maximumRentalDays, String location, String rentalInstructions, String rentalTerms, EquipmentStatus status, BigDecimal ratingAverage, Integer reviewCount, List<EquipmentImageDto> images, String primaryImageUrl, LocalDateTime createdAt) {
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
        this.images = images;
        this.primaryImageUrl = primaryImageUrl;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private UserDto owner;
        private CategoryDto category;
        private String name;
        private String slug;
        private String description;
        private String brand;
        private String model;
        private EquipmentCondition itemCondition;
        private BigDecimal dailyPrice;
        private BigDecimal securityDeposit;
        private Integer minimumRentalDays;
        private Integer maximumRentalDays;
        private String location;
        private String rentalInstructions;
        private String rentalTerms;
        private EquipmentStatus status;
        private BigDecimal ratingAverage;
        private Integer reviewCount;
        private List<EquipmentImageDto> images;
        private String primaryImageUrl;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder owner(UserDto owner) { this.owner = owner; return this; }
        public Builder category(CategoryDto category) { this.category = category; return this; }
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
        public Builder images(List<EquipmentImageDto> images) { this.images = images; return this; }
        public Builder primaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EquipmentDto build() {
            return new EquipmentDto(id, owner, category, name, slug, description, brand, model, itemCondition, dailyPrice, securityDeposit, minimumRentalDays, maximumRentalDays, location, rentalInstructions, rentalTerms, status, ratingAverage, reviewCount, images, primaryImageUrl, createdAt);
        }
    }

    public static class EquipmentImageDto {
        private Long id;
        private String filePath;
        private String altText;
        private Integer displayOrder;
        private Boolean isPrimary;

        public EquipmentImageDto() {}

        public EquipmentImageDto(Long id, String filePath, String altText, Integer displayOrder, Boolean isPrimary) {
            this.id = id;
            this.filePath = filePath;
            this.altText = altText;
            this.displayOrder = displayOrder;
            this.isPrimary = isPrimary;
        }

        public static ImageBuilder builder() { return new ImageBuilder(); }

        public static class ImageBuilder {
            private Long id;
            private String filePath;
            private String altText;
            private Integer displayOrder;
            private Boolean isPrimary;

            public ImageBuilder id(Long id) { this.id = id; return this; }
            public ImageBuilder filePath(String filePath) { this.filePath = filePath; return this; }
            public ImageBuilder altText(String altText) { this.altText = altText; return this; }
            public ImageBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
            public ImageBuilder isPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; return this; }

            public EquipmentImageDto build() {
                return new EquipmentImageDto(id, filePath, altText, displayOrder, isPrimary);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }

        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }

        public Integer getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

        public Boolean getIsPrimary() { return isPrimary; }
        public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDto getOwner() { return owner; }
    public void setOwner(UserDto owner) { this.owner = owner; }

    public CategoryDto getCategory() { return category; }
    public void setCategory(CategoryDto category) { this.category = category; }

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

    public List<EquipmentImageDto> getImages() { return images; }
    public void setImages(List<EquipmentImageDto> images) { this.images = images; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

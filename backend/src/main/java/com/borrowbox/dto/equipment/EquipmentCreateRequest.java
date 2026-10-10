package com.borrowbox.dto.equipment;

import com.borrowbox.entity.enums.EquipmentCondition;
import com.borrowbox.entity.enums.EquipmentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public class EquipmentCreateRequest {

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Equipment name is required")
    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private String brand;
    private String model;

    @NotNull(message = "Item condition is required")
    private EquipmentCondition itemCondition = EquipmentCondition.EXCELLENT;

    @NotNull(message = "Daily price is required")
    @DecimalMin(value = "0.01", message = "Daily price must be greater than zero")
    private BigDecimal dailyPrice;

    @NotNull(message = "Security deposit is required")
    @DecimalMin(value = "0.00", message = "Security deposit cannot be negative")
    private BigDecimal securityDeposit = BigDecimal.ZERO;

    @Min(value = 1, message = "Minimum rental duration must be at least 1 day")
    private Integer minimumRentalDays = 1;

    @Max(value = 365, message = "Maximum rental duration cannot exceed 365 days")
    private Integer maximumRentalDays = 30;

    @NotBlank(message = "Location is required")
    private String location;

    private String rentalInstructions;
    private String rentalTerms;

    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    private List<String> imageUrls;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}

package com.borrowbox.service;

import com.borrowbox.dto.equipment.EquipmentCreateRequest;
import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.dto.equipment.EquipmentSearchFilter;
import com.borrowbox.entity.Category;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.EquipmentImage;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.CategoryRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CategoryService categoryService;

    public EquipmentService(EquipmentRepository equipmentRepository,
            CategoryRepository categoryRepository, UserRepository userRepository, AuthService authService,
            CategoryService categoryService) {
        this.equipmentRepository = equipmentRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.categoryService = categoryService;
    }

    @Transactional
    public EquipmentDto createEquipment(String ownerEmail, EquipmentCreateRequest request) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (owner.getRole() == Role.ROLE_RENTER) {
            owner.setRole(Role.ROLE_OWNER);
            owner = userRepository.save(owner);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        String slug = generateSlug(request.getName());

        Equipment equipment = Equipment.builder()
                .owner(owner)
                .category(category)
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription().trim())
                .brand(request.getBrand() != null ? request.getBrand().trim() : null)
                .model(request.getModel() != null ? request.getModel().trim() : null)
                .itemCondition(request.getItemCondition())
                .dailyPrice(request.getDailyPrice())
                .securityDeposit(request.getSecurityDeposit() != null ? request.getSecurityDeposit() : BigDecimal.ZERO)
                .minimumRentalDays(request.getMinimumRentalDays() != null ? request.getMinimumRentalDays() : 1)
                .maximumRentalDays(request.getMaximumRentalDays() != null ? request.getMaximumRentalDays() : 30)
                .location(request.getLocation().trim())
                .rentalInstructions(request.getRentalInstructions())
                .rentalTerms(request.getRentalTerms())
                .status(request.getStatus() != null ? request.getStatus() : EquipmentStatus.AVAILABLE)
                .ratingAverage(BigDecimal.ZERO)
                .reviewCount(0)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                equipment.getImages().add(EquipmentImage.builder()
                        .equipment(equipment)
                        .filePath(request.getImageUrls().get(i))
                        .altText(equipment.getName() + " Image " + (i + 1))
                        .displayOrder(i + 1)
                        .isPrimary(i == 0)
                        .build());
            }
        } else {
            // Default placeholder image if none uploaded
            equipment.getImages().add(EquipmentImage.builder()
                    .equipment(equipment)
                    .filePath(
                            "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80")
                    .altText(equipment.getName())
                    .displayOrder(1)
                    .isPrimary(true)
                    .build());
        }

        equipment = equipmentRepository.save(equipment);
        return mapToDto(equipment);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentDto> searchEquipment(EquipmentSearchFilter filter) {
        Sort sort = switch (filter.getSortBy()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "dailyPrice");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "dailyPrice");
            case "rating" -> Sort.by(Sort.Direction.DESC, "ratingAverage");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Equipment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), EquipmentStatus.AVAILABLE));

            if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
                String q = "%" + filter.getQuery().toLowerCase().trim() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), q);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), q);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), q);
                predicates.add(cb.or(nameMatch, descMatch, brandMatch));
            }

            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            if (filter.getCategorySlug() != null && !filter.getCategorySlug().isBlank()) {
                predicates.add(cb.equal(root.get("category").get("slug"), filter.getCategorySlug()));
            }

            if (filter.getLocation() != null && !filter.getLocation().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("location")), "%" + filter.getLocation().toLowerCase().trim() + "%"));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dailyPrice"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dailyPrice"), filter.getMaxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return equipmentRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public EquipmentDto getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));
        return mapToDto(equipment);
    }

    @Transactional(readOnly = true)
    public List<EquipmentDto> getMyListings(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return equipmentRepository.findByOwnerIdOrderByIdDesc(owner.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipmentDto updateEquipment(Long id, String userEmail, EquipmentCreateRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + id));

        if (!equipment.getOwner().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedAccessException("You are not authorized to update another owner's listing!");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        equipment.setCategory(category);
        equipment.setName(request.getName().trim());
        equipment.setDescription(request.getDescription().trim());
        equipment.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        equipment.setModel(request.getModel() != null ? request.getModel().trim() : null);
        if (request.getItemCondition() != null) {
            equipment.setItemCondition(request.getItemCondition());
        }
        equipment.setDailyPrice(request.getDailyPrice());
        equipment.setSecurityDeposit(
                request.getSecurityDeposit() != null ? request.getSecurityDeposit() : BigDecimal.ZERO);
        equipment.setMinimumRentalDays(request.getMinimumRentalDays());
        equipment.setMaximumRentalDays(request.getMaximumRentalDays());
        equipment.setLocation(request.getLocation().trim());
        equipment.setRentalInstructions(request.getRentalInstructions());
        equipment.setRentalTerms(request.getRentalTerms());
        if (request.getStatus() != null) {
            equipment.setStatus(request.getStatus());
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            equipment.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                equipment.getImages().add(EquipmentImage.builder()
                        .equipment(equipment)
                        .filePath(request.getImageUrls().get(i))
                        .altText(equipment.getName() + " Image " + (i + 1))
                        .displayOrder(i + 1)
                        .isPrimary(i == 0)
                        .build());
            }
        }

        equipment = equipmentRepository.save(equipment);
        return mapToDto(equipment);
    }

    @Transactional
    public EquipmentDto updateStatus(Long id, String userEmail, EquipmentStatus status) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        if (!equipment.getOwner().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedAccessException("Not authorized to change status of this listing!");
        }

        equipment.setStatus(status);
        return mapToDto(equipmentRepository.save(equipment));
    }

    public EquipmentDto mapToDto(Equipment e) {
        List<EquipmentDto.EquipmentImageDto> imageDtos = e.getImages() == null ? new ArrayList<>()
                : e.getImages().stream()
                        .map(img -> EquipmentDto.EquipmentImageDto.builder()
                                .id(img.getId())
                                .filePath(img.getFilePath())
                                .altText(img.getAltText())
                                .displayOrder(img.getDisplayOrder())
                                .isPrimary(img.getIsPrimary())
                                .build())
                        .collect(Collectors.toList());

        String primaryUrl = imageDtos.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .map(img -> img != null ? img.getFilePath() : null)
                .filter(url -> url != null)
                .findFirst()
                .orElse(!imageDtos.isEmpty() && imageDtos.get(0) != null ? imageDtos.get(0).getFilePath()
                        : "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80");

        return EquipmentDto.builder()
                .id(e.getId())
                .owner(authService.mapToUserDto(e.getOwner()))
                .category(categoryService.mapToDto(e.getCategory()))
                .name(e.getName())
                .slug(e.getSlug())
                .description(e.getDescription())
                .brand(e.getBrand())
                .model(e.getModel())
                .itemCondition(e.getItemCondition())
                .dailyPrice(e.getDailyPrice())
                .securityDeposit(e.getSecurityDeposit())
                .minimumRentalDays(e.getMinimumRentalDays())
                .maximumRentalDays(e.getMaximumRentalDays())
                .location(e.getLocation())
                .rentalInstructions(e.getRentalInstructions())
                .rentalTerms(e.getRentalTerms())
                .status(e.getStatus())
                .ratingAverage(e.getRatingAverage())
                .reviewCount(e.getReviewCount())
                .images(imageDtos)
                .primaryImageUrl(primaryUrl)
                .createdAt(e.getCreatedAt())
                .build();
    }

    private String generateSlug(String name) {
        String base = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
        return base + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}

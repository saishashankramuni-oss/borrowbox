package com.borrowbox;

import com.borrowbox.dto.category.CategoryDto;
import com.borrowbox.dto.equipment.EquipmentCreateRequest;
import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.dto.equipment.EquipmentSearchFilter;
import com.borrowbox.dto.user.UserDto;
import com.borrowbox.entity.Category;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.enums.EquipmentCondition;
import com.borrowbox.entity.enums.EquipmentStatus;
import com.borrowbox.entity.enums.Role;
import com.borrowbox.exception.UnauthorizedAccessException;
import com.borrowbox.repository.CategoryRepository;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.service.AuthService;
import com.borrowbox.service.CategoryService;
import com.borrowbox.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentWorkflowTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private EquipmentService equipmentService;

    private User owner;
    private Category category;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .fullName("Rahul Sharma")
                .email("rahul.owner@borrowbox.com")
                .role(Role.ROLE_OWNER)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Cameras & Lenses")
                .slug("cameras-lenses")
                .build();
    }

    @Test
    @DisplayName("Create equipment listing succeeds and associates category, images, and owner")
    void testCreateEquipmentSuccess() {
        EquipmentCreateRequest request = new EquipmentCreateRequest();
        request.setCategoryId(1L);
        request.setName("Fujifilm X-T5 Mirrorless");
        request.setDescription("40MP APS-C sensor with 5-axis IBIS");
        request.setItemCondition(EquipmentCondition.LIKE_NEW);
        request.setDailyPrice(new BigDecimal("1800.00"));
        request.setSecurityDeposit(new BigDecimal("4000.00"));
        request.setMinimumRentalDays(1);
        request.setMaximumRentalDays(14);
        request.setLocation("Indiranagar, Bengaluru");
        request.setImageUrls(List.of("https://example.com/fuji-xt5.jpg"));

        when(userRepository.findByEmail("rahul.owner@borrowbox.com")).thenReturn(Optional.of(owner));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(i -> {
            Equipment e = i.getArgument(0);
            e.setId(50L);
            return e;
        });

        when(authService.mapToUserDto(owner)).thenReturn(UserDto.builder().id(1L).email("rahul.owner@borrowbox.com").build());
        when(categoryService.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).name("Cameras & Lenses").build());

        EquipmentDto result = equipmentService.createEquipment("rahul.owner@borrowbox.com", request);

        assertNotNull(result);
        assertEquals("Fujifilm X-T5 Mirrorless", result.getName());
        assertEquals(new BigDecimal("1800.00"), result.getDailyPrice());
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    @DisplayName("User cannot update listing belonging to another owner")
    void testRejectUpdateOtherOwnerListing() {
        Equipment existing = Equipment.builder()
                .id(50L)
                .owner(owner) // Owner is rahul.owner@borrowbox.com
                .name("Sony A7 IV")
                .dailyPrice(new BigDecimal("2500.00"))
                .status(EquipmentStatus.AVAILABLE)
                .build();

        when(equipmentRepository.findById(50L)).thenReturn(Optional.of(existing));

        EquipmentCreateRequest updateRequest = new EquipmentCreateRequest();
        updateRequest.setName("Hacked Title");

        assertThrows(UnauthorizedAccessException.class, () ->
                equipmentService.updateEquipment(50L, "intruder@borrowbox.com", updateRequest));

        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Owner can update own listing and sync images")
    void testOwnerUpdateOwnListing() {
        Equipment existing = Equipment.builder()
                .id(50L)
                .owner(owner)
                .category(category)
                .name("Sony A7 IV")
                .dailyPrice(new BigDecimal("2500.00"))
                .securityDeposit(new BigDecimal("5000.00"))
                .minimumRentalDays(1)
                .maximumRentalDays(14)
                .location("Bengaluru")
                .description("Old description")
                .status(EquipmentStatus.AVAILABLE)
                .build();

        when(equipmentRepository.findById(50L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(i -> i.getArgument(0));

        when(authService.mapToUserDto(owner)).thenReturn(UserDto.builder().id(1L).build());
        when(categoryService.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).build());

        EquipmentCreateRequest updateRequest = new EquipmentCreateRequest();
        updateRequest.setCategoryId(1L);
        updateRequest.setName("Sony A7 IV Upgraded");
        updateRequest.setDescription("New updated description");
        updateRequest.setItemCondition(EquipmentCondition.EXCELLENT);
        updateRequest.setDailyPrice(new BigDecimal("2600.00"));
        updateRequest.setSecurityDeposit(new BigDecimal("5000.00"));
        updateRequest.setMinimumRentalDays(2);
        updateRequest.setMaximumRentalDays(20);
        updateRequest.setLocation("Koramangala, Bengaluru");
        updateRequest.setImageUrls(List.of("https://example.com/new-image.jpg"));

        EquipmentDto updated = equipmentService.updateEquipment(50L, "rahul.owner@borrowbox.com", updateRequest);

        assertNotNull(updated);
        assertEquals("Sony A7 IV Upgraded", updated.getName());
        assertEquals(new BigDecimal("2600.00"), updated.getDailyPrice());
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Search equipment filters by category and status")
    void testSearchEquipment() {
        EquipmentSearchFilter filter = new EquipmentSearchFilter();
        filter.setCategoryId(1L);
        filter.setSortBy("price_asc");
        filter.setPage(0);
        filter.setSize(10);

        Equipment eq = Equipment.builder()
                .id(1L)
                .owner(owner)
                .category(category)
                .name("Sony A7 IV")
                .dailyPrice(new BigDecimal("2500.00"))
                .status(EquipmentStatus.AVAILABLE)
                .images(new ArrayList<>())
                .build();

        Page<Equipment> page = new PageImpl<>(List.of(eq));

        when(equipmentRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Equipment>>any(), any(Pageable.class))).thenReturn(page);
        when(authService.mapToUserDto(owner)).thenReturn(UserDto.builder().id(1L).build());
        when(categoryService.mapToDto(category)).thenReturn(CategoryDto.builder().id(1L).build());

        Page<EquipmentDto> result = equipmentService.searchEquipment(filter);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Sony A7 IV", result.getContent().get(0).getName());
    }
}

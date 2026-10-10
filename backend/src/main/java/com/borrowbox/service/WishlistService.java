package com.borrowbox.service;

import com.borrowbox.dto.equipment.EquipmentDto;
import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.User;
import com.borrowbox.entity.Wishlist;
import com.borrowbox.exception.ResourceNotFoundException;
import com.borrowbox.repository.EquipmentRepository;
import com.borrowbox.repository.UserRepository;
import com.borrowbox.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final EquipmentService equipmentService;

    public WishlistService(WishlistRepository wishlistRepository, EquipmentRepository equipmentRepository, UserRepository userRepository, EquipmentService equipmentService) {
        this.wishlistRepository = wishlistRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.equipmentService = equipmentService;
    }

    @Transactional
    public boolean toggleWishlist(String userEmail, Long equipmentId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with ID: " + equipmentId));

        return wishlistRepository.findByUserIdAndEquipmentId(user.getId(), equipmentId)
                .map(w -> {
                    wishlistRepository.delete(w);
                    return false; // Removed
                })
                .orElseGet(() -> {
                    Wishlist wishlist = Wishlist.builder()
                            .user(user)
                            .equipment(equipment)
                            .build();
                    wishlistRepository.save(wishlist);
                    return true; // Added
                });
    }

    @Transactional(readOnly = true)
    public List<EquipmentDto> getMyWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(Wishlist::getEquipment)
                .filter(java.util.Objects::nonNull)
                .map(equipmentService::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(String userEmail, Long equipmentId) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;
        return wishlistRepository.existsByUserIdAndEquipmentId(user.getId(), equipmentId);
    }
}

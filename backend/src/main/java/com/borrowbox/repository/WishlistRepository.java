package com.borrowbox.repository;

import com.borrowbox.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Wishlist> findByUserIdAndEquipmentId(Long userId, Long equipmentId);
    boolean existsByUserIdAndEquipmentId(Long userId, Long equipmentId);
    void deleteByUserIdAndEquipmentId(Long userId, Long equipmentId);
}

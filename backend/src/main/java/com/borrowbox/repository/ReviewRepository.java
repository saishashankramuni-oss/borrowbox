package com.borrowbox.repository;

import com.borrowbox.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEquipmentIdOrderByCreatedAtDesc(Long equipmentId);
    Optional<Review> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.equipment.id = :equipmentId")
    Double calculateAverageRating(@Param("equipmentId") Long equipmentId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.equipment.id = :equipmentId")
    long countByEquipmentId(@Param("equipmentId") Long equipmentId);
}

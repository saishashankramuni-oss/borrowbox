package com.borrowbox.repository;

import com.borrowbox.entity.Equipment;
import com.borrowbox.entity.enums.EquipmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {

    Optional<Equipment> findBySlug(String slug);

    List<Equipment> findByOwnerIdOrderByIdDesc(Long ownerId);

    long countByOwnerId(Long ownerId);

    long countByOwnerIdAndStatus(Long ownerId, EquipmentStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Equipment e WHERE e.id = :id")
    Optional<Equipment> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT e FROM Equipment e WHERE e.status = 'AVAILABLE' ORDER BY e.createdAt DESC")
    Page<Equipment> findFeatured(Pageable pageable);
}

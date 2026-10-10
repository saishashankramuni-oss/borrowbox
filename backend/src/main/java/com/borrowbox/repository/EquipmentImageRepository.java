package com.borrowbox.repository;

import com.borrowbox.entity.EquipmentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentImageRepository extends JpaRepository<EquipmentImage, Long> {
    List<EquipmentImage> findByEquipmentIdOrderByDisplayOrderAsc(Long equipmentId);
}

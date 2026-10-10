package com.borrowbox.repository;

import com.borrowbox.entity.Dispute;
import com.borrowbox.entity.enums.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatusOrderByCreatedAtDesc(DisputeStatus status);
    List<Dispute> findAllByOrderByCreatedAtDesc();
    long countByStatus(DisputeStatus status);
}

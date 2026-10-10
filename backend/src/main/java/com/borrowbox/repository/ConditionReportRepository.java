package com.borrowbox.repository;

import com.borrowbox.entity.ConditionReport;
import com.borrowbox.entity.enums.InspectionStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConditionReportRepository extends JpaRepository<ConditionReport, Long> {
    List<ConditionReport> findByBookingIdOrderByCreatedAtAsc(Long bookingId);
    Optional<ConditionReport> findByBookingIdAndInspectionStage(Long bookingId, InspectionStage stage);
}

package com.borrowbox.repository;

import com.borrowbox.entity.ConditionReportImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConditionReportImageRepository extends JpaRepository<ConditionReportImage, Long> {
    List<ConditionReportImage> findByConditionReportId(Long conditionReportId);
}

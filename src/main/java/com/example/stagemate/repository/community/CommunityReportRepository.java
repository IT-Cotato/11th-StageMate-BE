package com.example.stagemate.repository.community;

import com.example.stagemate.domain.community.CommunityReport;
import com.example.stagemate.domain.community.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, ReportTargetType targetType, Long targetId);
}

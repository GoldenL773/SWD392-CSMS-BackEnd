package com.csms.report.repository;

import com.csms.report.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportFileRepository extends JpaRepository<ReportFile, Long> {
    List<ReportFile> findAllByOrderByCreatedAtDesc();
    List<ReportFile> findByReportType(String reportType);
}

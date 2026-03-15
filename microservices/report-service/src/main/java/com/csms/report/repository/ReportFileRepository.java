package com.csms.report.repository;

import com.csms.report.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportFileRepository extends JpaRepository<ReportFile, Long> {
}
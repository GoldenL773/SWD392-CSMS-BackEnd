package com.csms.report.repository;

import com.csms.report.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    Optional<DailyReport> findByReportDate(LocalDate reportDate);
    List<DailyReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
    List<DailyReport> findByReportDateBetweenOrderByReportDateDesc(LocalDate startDate, LocalDate endDate);
    List<DailyReport> findAllByOrderByReportDateDesc();
}


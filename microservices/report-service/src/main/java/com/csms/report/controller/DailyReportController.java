package com.csms.report.controller;

import com.csms.report.entity.DailyReport;
import com.csms.report.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportRepository dailyReportRepository;

    @GetMapping("/daily")
    public ResponseEntity<List<DailyReport>> getDailyReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from != null && to != null) {
            return ResponseEntity.ok(dailyReportRepository.findByReportDateBetweenOrderByReportDateDesc(from, to));
        }
        return ResponseEntity.ok(dailyReportRepository.findAllByOrderByReportDateDesc());
    }

    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyReport> getDailyReportByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyReportRepository.findByReportDate(date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

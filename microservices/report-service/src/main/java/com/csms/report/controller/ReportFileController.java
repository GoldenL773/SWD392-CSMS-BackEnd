package com.csms.report.controller;

import com.csms.report.entity.ReportFile;
import com.csms.report.repository.ReportFileRepository;
import com.csms.report.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportFileController {

    private final ReportFileRepository reportFileRepository;
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ReportFile> uploadReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("reportType") String reportType,
            @RequestParam("reportPeriod") String reportPeriod) {

        String fileName = fileStorageService.storeFile(file);

        ReportFile reportFile = ReportFile.builder()
                .title(title)
                .description(description)
                .reportType(reportType)
                .reportPeriod(reportPeriod)
                .fileName(fileName)
                .filePath(fileName)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        return ResponseEntity.ok(reportFileRepository.save(reportFile));
    }

    @GetMapping("/files")
    public ResponseEntity<List<ReportFile>> getAllReportFiles() {
        return ResponseEntity.ok(reportFileRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        ReportFile reportFile = reportFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));

        Resource resource = fileStorageService.loadFileAsResource(reportFile.getFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(reportFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportFile.getTitle() + "\"")
                .body(resource);
    }
}

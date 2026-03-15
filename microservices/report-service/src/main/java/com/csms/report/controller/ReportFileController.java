package com.csms.report.controller;

import com.csms.report.entity.ReportFile;
import com.csms.report.repository.ReportFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportFileController {

    private final ReportFileRepository reportFileRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReportFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("reportType") String reportType,
            @RequestParam("reportPeriod") String reportPeriod) {

        try {
            ReportFile reportFile = ReportFile.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileData(file.getBytes())
                    .title(title)
                    .description(description)
                    .reportType(reportType)
                    .reportPeriod(reportPeriod)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            reportFileRepository.save(reportFile);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "File uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to upload file"));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<Map<String, Object>>> getUploadedReports() {
        List<Map<String, Object>> files = reportFileRepository.findAll().stream()
                .map(rf -> Map.of(
                        "id", rf.getId(),
                        "fileName", rf.getFileName(),
                        "fileType", rf.getFileType() != null ? rf.getFileType() : "application/octet-stream",
                        "title", rf.getTitle(),
                        "description", rf.getDescription() != null ? rf.getDescription() : "",
                        "reportType", rf.getReportType(),
                        "reportPeriod", rf.getReportPeriod(),
                        "uploadedAt", rf.getUploadedAt().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadReportFile(@PathVariable Long id) {
        ReportFile reportFile = reportFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));

        String fileType = reportFile.getFileType() != null ? reportFile.getFileType() : "application/octet-stream";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(fileType))
                .body(reportFile.getFileData());
    }
}
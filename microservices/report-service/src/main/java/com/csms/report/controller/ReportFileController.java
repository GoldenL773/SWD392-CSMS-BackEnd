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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload file: " + e.getMessage()));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<Map<String, Object>>> getUploadedReports() {
        List<Map<String, Object>> files = reportFileRepository.findAll().stream()
                .map(rf -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", rf.getId());
                    map.put("fileName", rf.getFileName());
                    map.put("fileType", rf.getFileType() != null ? rf.getFileType() : "application/octet-stream");
                    map.put("title", rf.getTitle() != null ? rf.getTitle() : rf.getFileName());
                    map.put("description", rf.getDescription() != null ? rf.getDescription() : "");
                    map.put("reportType", rf.getReportType());
                    map.put("reportPeriod", rf.getReportPeriod());
                    map.put("fileSize", rf.getFileData() != null ? rf.getFileData().length : 0);
                    map.put("uploadedAt", rf.getUploadedAt() != null ? rf.getUploadedAt().toString() : java.time.LocalDateTime.now().toString());
                    return map;
                })
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
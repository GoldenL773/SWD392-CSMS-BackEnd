package com.csms.report.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @Column(columnDefinition = "VARBINARY(MAX)")
    private byte[] fileData;

    private String title;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    private String reportType;
    private String reportPeriod;

    private LocalDateTime uploadedAt;
}
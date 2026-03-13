package com.example.adminService.Dto.quiz;

import java.time.LocalDateTime;

/**
 * ExcelFileDTO
 * Representerar en uppladdad Excel-fil
 */
public class ExcelFileDTO {
    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private boolean dryRun;

    public ExcelFileDTO() {
    }

    public ExcelFileDTO(Long id, String fileName, LocalDateTime uploadedAt, boolean dryRun) {
        this.id = id;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.dryRun = dryRun;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
}

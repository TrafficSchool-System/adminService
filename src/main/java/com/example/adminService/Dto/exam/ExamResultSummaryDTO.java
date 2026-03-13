package com.example.adminService.Dto.exam;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResultSummaryDTO {
    private Long id;
    private int score;
    private int totalQuestions;
    private boolean passed;
    private int timeTaken; // minuter
    private LocalDateTime finishedAt;
    private int percentage;
}

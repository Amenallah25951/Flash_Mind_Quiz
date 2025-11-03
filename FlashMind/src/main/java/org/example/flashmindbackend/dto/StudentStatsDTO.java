package org.example.flashmindbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatsDTO {
    private String studentName;
    private String username;
    private Integer totalQuizzes;
    private BigDecimal averageScore;
    private Integer currentStreak;
    private BigDecimal bestScore;
    private Integer perfectQuizzes;
    private BigDecimal successRate;
}

package org.example.flashmindbackend.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QuizHistoryDTO {
    private Integer participationId;
    private Integer quizId;
    private String quizTitle;
    private String quizDescription;
    private BigDecimal score;
    private LocalDateTime completedAt;
    private String professorName;
    private Integer rank;
    private Integer totalParticipants;
}
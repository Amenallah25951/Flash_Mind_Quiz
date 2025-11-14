package org.example.flashmindbackend.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParticipationDTO {
    private Integer id;
    private Integer quizId;
    private String quizTitle;
    private BigDecimal score;
    private LocalDateTime completedAt;
    private Integer duration;
    private Integer questionCount;
    private Integer correctAnswers;
}
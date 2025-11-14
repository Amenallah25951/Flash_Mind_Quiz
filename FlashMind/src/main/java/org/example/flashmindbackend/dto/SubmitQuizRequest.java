package org.example.flashmindbackend.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SubmitQuizRequest {
    private Integer quizId;
    private BigDecimal score;
    private BigDecimal percentage;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Integer totalTime;
    private List<AnswerSubmission> answers;

    @Data
    public static class AnswerSubmission {
        private Integer questionId;
        private Integer selectedResponseId;
        private Boolean isCorrect;
        private Integer timeSpent;
    }
}
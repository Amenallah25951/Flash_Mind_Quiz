package org.example.flashmindbackend.dto;

import lombok.Data;

@Data
public class QuizDTO {
    private Integer id;
    private String title;
    private String description;
    private String code;
    private Integer duration;
    private Integer questionCount;
    private String professorName;
    private String difficulty;
}

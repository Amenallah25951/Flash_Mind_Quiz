package org.example.flashmindbackend.dto;


import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Integer id;
    private String questionText;
    private List<ResponseDTO> responses;
}
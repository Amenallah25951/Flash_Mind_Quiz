package org.example.flashmindbackend.dto;


import lombok.Data;

@Data
public class ResponseDTO {
    private Integer id;
    private String responseText;
    private Boolean isCorrect; // Ne pas exposer dans certains cas
}
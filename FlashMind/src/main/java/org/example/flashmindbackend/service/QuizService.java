package org.example.flashmindbackend.service;


import org.example.flashmindbackend.dto.QuizDTO;
import org.example.flashmindbackend.entity.Quiz;
import org.example.flashmindbackend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;

    public List<QuizDTO> getAllPublicQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuizDTO getQuizByCode(String code) {
        Quiz quiz = quizRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Quiz not found with code: " + code));
        return convertToDTO(quiz);
    }

    public QuizDTO getQuizById(Integer id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        return convertToDTO(quiz);
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCode(quiz.getCode());
        dto.setDuration(quiz.getDuration());
        dto.setQuestionCount(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
        dto.setProfessorName(quiz.getProfessor().getFirstName() + " " + quiz.getProfessor().getLastName());
        return dto;
    }

    public String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (quizRepository.findByCode(code).isPresent());
        return code;
    }

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }
}
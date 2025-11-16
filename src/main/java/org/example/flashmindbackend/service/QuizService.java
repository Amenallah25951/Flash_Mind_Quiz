package org.example.flashmindbackend.service;


import org.example.flashmindbackend.dto.*;
import org.example.flashmindbackend.entity.*;
import org.example.flashmindbackend.repository.*;
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
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public List<QuizDTO> getAllPublicQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();

        return quizzes.stream()
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

    public List<QuestionDTO> getQuizQuestions(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        return quiz.getQuestions().stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void submitQuiz(Integer quizId, String userEmail, SubmitQuizRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Créer une participation
        Participation participation = new Participation();
        participation.setQuiz(quiz);
        participation.setUser(user);
        participation.setScore(request.getScore());

        participationRepository.save(participation);
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

        // Calculer la difficulté (exemple simple)
        if (quiz.getQuestions() != null) {
            int questionCount = quiz.getQuestions().size();
            if (questionCount <= 10) {
                dto.setDifficulty("Facile");
            } else if (questionCount <= 15) {
                dto.setDifficulty("Moyen");
            } else {
                dto.setDifficulty("Difficile");
            }
        }

        return dto;
    }

    private QuestionDTO convertToQuestionDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());

        List<ResponseDTO> responses = question.getResponses().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        dto.setResponses(responses);
        return dto;
    }

    private ResponseDTO convertToResponseDTO(Response response) {
        ResponseDTO dto = new ResponseDTO();
        dto.setId(response.getId());
        dto.setResponseText(response.getResponseText());
        dto.setIsCorrect(response.getIsCorrect());
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
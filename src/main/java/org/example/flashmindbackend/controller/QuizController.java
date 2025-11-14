package org.example.flashmindbackend.controller;


import org.example.flashmindbackend.dto.QuizDTO;
import org.example.flashmindbackend.dto.QuestionDTO;
import org.example.flashmindbackend.dto.SubmitQuizRequest;
import org.example.flashmindbackend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")  // Changed base path
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('STUDENT')")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/public")
    public ResponseEntity<List<QuizDTO>> getPublicQuizzes() {
        try {
            List<QuizDTO> quizzes = quizService.getAllPublicQuizzes();
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getQuizByCode(@PathVariable String code) {
        try {
            QuizDTO quiz = quizService.getQuizByCode(code);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Integer id) {
        try {
            QuizDTO quiz = quizService.getQuizById(id);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuizQuestions(@PathVariable Integer id) {
        try {
            List<QuestionDTO> questions = quizService.getQuizQuestions(id);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Integer id,
            @RequestBody SubmitQuizRequest request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            quizService.submitQuiz(id, email, request);
            return ResponseEntity.ok().body("Quiz submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
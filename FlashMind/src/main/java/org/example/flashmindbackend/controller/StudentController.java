package org.example.flashmindbackend.controller;



import org.example.flashmindbackend.dto.ParticipationDTO;
import org.example.flashmindbackend.dto.QuizDTO;
import org.example.flashmindbackend.dto.QuizHistoryDTO;
import org.example.flashmindbackend.dto.StudentStatsDTO;
import org.example.flashmindbackend.service.QuizService;
import org.example.flashmindbackend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final QuizService quizService;
    private final StudentService studentService;

    @GetMapping("/quizzes/public")
    public ResponseEntity<List<QuizDTO>> getPublicQuizzes() {
        return ResponseEntity.ok(quizService.getAllPublicQuizzes());
    }

    @GetMapping("/quiz/code/{code}")
    public ResponseEntity<?> getQuizByCode(@PathVariable String code) {
        try {
            QuizDTO quiz = quizService.getQuizByCode(code);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quiz/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Integer id) {
        try {
            QuizDTO quiz = quizService.getQuizById(id);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStudentStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            StudentStatsDTO stats = studentService.getStudentStats(email);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/history")
    public ResponseEntity<?> getQuizHistory(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<QuizHistoryDTO> history = studentService.getQuizHistory(email);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/participation/{id}")
    public ResponseEntity<?> getParticipationDetails(
            Authentication authentication,
            @PathVariable Integer id
    ) {
        try {
            String email = authentication.getName();
            ParticipationDTO participation = studentService.getParticipationDetails(email, id);
            return ResponseEntity.ok(participation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<StudentStatsDTO> leaderboard = studentService.getGlobalLeaderboard(limit);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
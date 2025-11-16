package org.example.flashmindbackend.controller;

import org.example.flashmindbackend.dto.ParticipationDTO;
import org.example.flashmindbackend.dto.QuizHistoryDTO;
import org.example.flashmindbackend.dto.StudentStatsDTO;
import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.Quiz;
import org.example.flashmindbackend.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    /**
     * Récupère les informations du profil de l'étudiant connecté
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Récupération du profil pour: {}", email);

            Student student = studentService.getStudentByEmail(email);

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", student.getId());
            profile.put("firstName", student.getFirstName());
            profile.put("lastName", student.getLastName());
            profile.put("username", student.getUsers().getUsername());
            profile.put("email", student.getUsers().getEmail());
            profile.put("createdAt", student.getCreatedAt());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du profil: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du profil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Met à jour le profil de l'étudiant
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, String> updates
    ) {
        try {
            String email = authentication.getName();
            String firstName = updates.get("firstName");
            String lastName = updates.get("lastName");

            log.info("Mise à jour du profil pour: {}", email);

            Student student = studentService.updateStudentProfile(email, firstName, lastName);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Profil mis à jour avec succès");
            response.put("firstName", student.getFirstName());
            response.put("lastName", student.getLastName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du profil: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la mise à jour du profil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère les statistiques de l'étudiant connecté
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStudentStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Récupération des statistiques pour: {}", email);

            StudentStatsDTO stats = studentService.getStudentStats(email);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.error("Étudiant non trouvé: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Étudiant non trouvé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère les statistiques détaillées
     */
    @GetMapping("/stats/detailed")
    public ResponseEntity<?> getDetailedStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Récupération des statistiques détaillées pour: {}", email);

            StudentStatsDTO stats = studentService.getDetailedStats(email);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques détaillées: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des statistiques détaillées: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère l'historique des quiz de l'étudiant
     */
    @GetMapping("/history")
    public ResponseEntity<?> getQuizHistory(Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Récupération de l'historique pour: {}", email);

            List<QuizHistoryDTO> history = studentService.getQuizHistory(email);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            log.error("Étudiant non trouvé: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Étudiant non trouvé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'historique: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération de l'historique: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère les détails d'une participation spécifique
     */
    @GetMapping("/participation/{id}")
    public ResponseEntity<?> getParticipationDetails(
            Authentication authentication,
            @PathVariable Integer id
    ) {
        try {
            String email = authentication.getName();
            log.info("Récupération des détails de la participation {} pour: {}", id, email);

            ParticipationDTO participation = studentService.getParticipationDetails(email, id);
            return ResponseEntity.ok(participation);
        } catch (RuntimeException e) {
            log.error("Erreur: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des détails: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des détails: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère le classement global
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            log.info("Récupération du classement (limite: {})", limit);

            List<StudentStatsDTO> leaderboard = studentService.getGlobalLeaderboard(limit);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du classement: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du classement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère les étudiants les plus actifs
     */
    @GetMapping("/most-active")
    public ResponseEntity<?> getMostActiveStudents(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            log.info("Récupération des étudiants les plus actifs (limite: {})", limit);

            List<StudentStatsDTO> activeStudents = studentService.getMostActiveStudents(limit);
            return ResponseEntity.ok(activeStudents);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des étudiants actifs: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des étudiants actifs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère les quiz recommandés pour l'étudiant
     */
    @GetMapping("/recommended-quizzes")
    public ResponseEntity<?> getRecommendedQuizzes(
            Authentication authentication,
            @RequestParam(defaultValue = "5") int limit
    ) {
        try {
            String email = authentication.getName();
            log.info("Récupération des quiz recommandés pour: {} (limite: {})", email, limit);

            List<Quiz> recommendedQuizzes = studentService.getRecommendedQuizzes(email, limit);
            return ResponseEntity.ok(recommendedQuizzes);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des quiz recommandés: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des quiz recommandés: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Vérifie si l'étudiant peut participer à un quiz
     */
    @GetMapping("/quiz/{id}/can-participate")
    public ResponseEntity<?> canParticipate(
            Authentication authentication,
            @PathVariable Integer id
    ) {
        try {
            String email = authentication.getName();
            log.debug("Vérification de participation au quiz {} pour: {}", id, email);

            boolean canParticipate = studentService.canParticipateInQuiz(email, id);

            Map<String, Object> response = new HashMap<>();
            response.put("canParticipate", canParticipate);
            response.put("message", canParticipate ?
                    "Vous pouvez participer à ce quiz" :
                    "Vous avez déjà participé à ce quiz");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la vérification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Récupère le nombre total de quiz disponibles
     */
    @GetMapping("/stats/summary")
    public ResponseEntity<?> getStatsSummary(Authentication authentication) {
        try {
            String email = authentication.getName();
            log.info("Récupération du résumé des statistiques pour: {}", email);

            StudentStatsDTO stats = studentService.getStudentStats(email);

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalQuizzes", stats.getTotalQuizzes());
            summary.put("averageScore", stats.getAverageScore());
            summary.put("currentStreak", stats.getCurrentStreak());
            summary.put("successRate", stats.getSuccessRate());

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du résumé: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération du résumé: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
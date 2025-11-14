package org.example.flashmindbackend.service;

import org.example.flashmindbackend.dto.StudentStatsDTO;
import org.example.flashmindbackend.dto.ParticipationDTO;
import org.example.flashmindbackend.dto.QuizHistoryDTO;
import org.example.flashmindbackend.entity.Participation;
import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.User;
import org.example.flashmindbackend.entity.Quiz;
import org.example.flashmindbackend.repository.ParticipationRepository;
import org.example.flashmindbackend.repository.StudentRepository;
import org.example.flashmindbackend.repository.UserRepository;
import org.example.flashmindbackend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final QuizRepository quizRepository;

    /**
     * Récupère un étudiant par son email
     */
    public Student getStudentByEmail(String email) {
        log.debug("Récupération de l'étudiant avec l'email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));

        if (user.getRole() != User.Role.student) {
            throw new RuntimeException("L'utilisateur n'est pas un étudiant");
        }

        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil étudiant non trouvé"));
    }

    /**
     * Récupère un étudiant par son ID utilisateur
     */
    public Student getStudentByUserId(Integer userId) {
        log.debug("Récupération de l'étudiant avec l'ID utilisateur: {}", userId);

        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'ID utilisateur: " + userId));
    }

    /**
     * Récupère les statistiques complètes d'un étudiant
     */
    public StudentStatsDTO getStudentStats(String email) {
        log.info("Calcul des statistiques pour l'étudiant: {}", email);

        Student student = getStudentByEmail(email);
        List<Participation> participations = participationRepository.findByUserId((long) student.getUser().getId());

        StudentStatsDTO stats = new StudentStatsDTO();

        // Informations de l'étudiant
        stats.setStudentName(student.getFirstName() + " " + student.getLastName());
        stats.setUsername(student.getUser().getUsername());

        // Nombre total de quiz complétés
        stats.setTotalQuizzes(participations.size());

        // Score moyen
        stats.setAverageScore(calculateAverageScore(participations));

        // Série actuelle (streak)
        stats.setCurrentStreak(calculateStreak(participations));

        // Meilleur score
        stats.setBestScore(calculateBestScore(participations));

        // Nombre de quiz parfaits (score = 100%)
        stats.setPerfectQuizzes(calculatePerfectQuizzes(participations));

        // Taux de réussite moyen (en pourcentage)
        stats.setSuccessRate(calculateSuccessRate(participations));

        log.info("Statistiques calculées avec succès pour {}: {} quiz, {}% de réussite",
                email, stats.getTotalQuizzes(), stats.getSuccessRate());

        return stats;
    }

    /**
     * Récupère les statistiques détaillées d'un étudiant
     */
    public StudentStatsDTO getDetailedStats(String email) {
        log.info("Récupération des statistiques détaillées pour: {}", email);

        StudentStatsDTO stats = getStudentStats(email);

        // Vous pouvez ajouter des statistiques supplémentaires ici
        // Par exemple : performance par catégorie, temps moyen, etc.

        return stats;
    }

    /**
     * Calcule le score moyen
     */
    private BigDecimal calculateAverageScore(List<Participation> participations) {
        if (participations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = participations.stream()
                .map(Participation::getScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalScore.divide(
                new BigDecimal(participations.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Calcule la série de jours consécutifs avec au moins un quiz
     */
    private int calculateStreak(List<Participation> participations) {
        if (participations.isEmpty()) {
            return 0;
        }

        // Trier les participations par date décroissante
        List<LocalDate> dates = participations.stream()
                .map(p -> p.getCreatedAt().toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (dates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Vérifier si l'étudiant a fait un quiz aujourd'hui ou hier
        if (!dates.get(0).equals(today) && !dates.get(0).equals(yesterday)) {
            return 0;
        }

        int streak = 1;
        for (int i = 0; i < dates.size() - 1; i++) {
            long daysBetween = ChronoUnit.DAYS.between(dates.get(i + 1), dates.get(i));
            if (daysBetween == 1) {
                streak++;
            } else {
                break;
            }
        }

        log.debug("Série calculée: {} jours", streak);
        return streak;
    }

    /**
     * Calcule le meilleur score obtenu
     */
    private BigDecimal calculateBestScore(List<Participation> participations) {
        return participations.stream()
                .map(Participation::getScore)
                .filter(score -> score != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Compte le nombre de quiz avec un score parfait (100%)
     */
    private int calculatePerfectQuizzes(List<Participation> participations) {
        return (int) participations.stream()
                .filter(p -> p.getScore() != null)
                .filter(p -> p.getScore().compareTo(new BigDecimal("100")) == 0)
                .count();
    }

    /**
     * Calcule le taux de réussite moyen (en pourcentage)
     */
    private BigDecimal calculateSuccessRate(List<Participation> participations) {
        if (participations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = participations.stream()
                .map(Participation::getScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Score maximum par quiz = 100
        BigDecimal maxPossibleScore = new BigDecimal(participations.size() * 100);

        if (maxPossibleScore.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalScore.divide(maxPossibleScore, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Récupère l'historique des quiz d'un étudiant
     */
    public List<QuizHistoryDTO> getQuizHistory(String email) {
        log.info("Récupération de l'historique des quiz pour: {}", email);

        Student student = getStudentByEmail(email);
        List<Participation> participations = participationRepository.findByUserId((long) student.getUser().getId());

        List<QuizHistoryDTO> history = participations.stream()
                .sorted(Comparator.comparing(Participation::getCreatedAt).reversed())
                .map(this::convertToQuizHistoryDTO)
                .collect(Collectors.toList());

        log.info("Historique récupéré: {} participations", history.size());
        return history;
    }

    /**
     * Récupère les détails d'une participation spécifique
     */
    public ParticipationDTO getParticipationDetails(String email, Integer participationId) {
        log.debug("Récupération des détails de la participation {} pour {}", participationId, email);

        Student student = getStudentByEmail(email);
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée"));

        // Vérifier que la participation appartient bien à l'étudiant
        if (!(participation.getUser().getId() ==(student.getUser().getId()))) {
            throw new RuntimeException("Accès non autorisé à cette participation");
        }

        return convertToParticipationDTO(participation);
    }

    /**
     * Met à jour le profil de l'étudiant
     */
    public Student updateStudentProfile(String email, String firstName, String lastName) {
        log.info("Mise à jour du profil pour: {}", email);

        Student student = getStudentByEmail(email);

        if (firstName != null && !firstName.trim().isEmpty()) {
            student.setFirstName(firstName.trim());
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            student.setLastName(lastName.trim());
        }

        Student updatedStudent = studentRepository.save(student);
        log.info("Profil mis à jour avec succès pour: {}", email);

        return updatedStudent;
    }

    /**
     * Récupère le classement global des étudiants
     */
    public List<StudentStatsDTO> getGlobalLeaderboard(int limit) {
        log.info("Récupération du classement global (limite: {})", limit);

        List<Student> allStudents = studentRepository.findAll();

        List<StudentStatsDTO> leaderboard = allStudents.stream()
                .map(student -> {
                    List<Participation> participations = participationRepository
                            .findByUserId((long) student.getUser().getId());

                    StudentStatsDTO stats = new StudentStatsDTO();
                    stats.setStudentName(student.getFirstName() + " " + student.getLastName());
                    stats.setUsername(student.getUser().getUsername());
                    stats.setTotalQuizzes(participations.size());
                    stats.setAverageScore(calculateAverageScore(participations));

                    return stats;
                })
                .filter(stats -> stats.getTotalQuizzes() > 0) // Exclure les étudiants sans participation
                .sorted(Comparator.comparing(StudentStatsDTO::getAverageScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        log.info("Classement récupéré: {} étudiants", leaderboard.size());
        return leaderboard;
    }

    /**
     * Récupère les étudiants les plus actifs
     */
    public List<StudentStatsDTO> getMostActiveStudents(int limit) {
        log.info("Récupération des étudiants les plus actifs (limite: {})", limit);

        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .map(student -> {
                    List<Participation> participations = participationRepository
                            .findByUserId((long) student.getUser().getId());

                    StudentStatsDTO stats = new StudentStatsDTO();
                    stats.setStudentName(student.getFirstName() + " " + student.getLastName());
                    stats.setUsername(student.getUser().getUsername());
                    stats.setTotalQuizzes(participations.size());
                    stats.setAverageScore(calculateAverageScore(participations));

                    return stats;
                })
                .filter(stats -> stats.getTotalQuizzes() > 0)
                .sorted(Comparator.comparing(StudentStatsDTO::getTotalQuizzes).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un étudiant peut participer à un quiz
     */
    public boolean canParticipateInQuiz(String email, Integer quizId) {
        log.debug("Vérification de la possibilité de participation au quiz {} pour {}", quizId, email);

        Student student = getStudentByEmail(email);
        List<Participation> participations = participationRepository.findByUserId((long) student.getUser().getId());

        boolean alreadyParticipated = participations.stream()
                .anyMatch(p -> p.getQuiz().getId().equals(quizId));

        log.debug("L'étudiant {} {} déjà participé au quiz {}",
                email, alreadyParticipated ? "a" : "n'a pas", quizId);

        return !alreadyParticipated;
    }

    /**
     * Enregistre une nouvelle participation
     */
    public Participation createParticipation(String email, Integer quizId, BigDecimal score) {
        log.info("Création d'une nouvelle participation pour {} au quiz {}", email, quizId);

        Student student = getStudentByEmail(email);

        if (!canParticipateInQuiz(email, quizId)) {
            throw new RuntimeException("L'étudiant a déjà participé à ce quiz");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé avec l'ID: " + quizId));

        Participation participation = new Participation();
        participation.setUser(student.getUser());
        participation.setQuiz(quiz);
        participation.setScore(score);
        participation.setCreatedAt(LocalDateTime.now());

        Participation savedParticipation = participationRepository.save(participation);
        log.info("Participation créée avec succès: ID {}", savedParticipation.getId());

        return savedParticipation;
    }

    /**
     * Récupère les quiz recommandés pour un étudiant (quiz non encore complétés)
     */
    public List<Quiz> getRecommendedQuizzes(String email, int limit) {
        log.info("Récupération des quiz recommandés pour: {} (limite: {})", email, limit);

        Student student = getStudentByEmail(email);
        List<Participation> completedParticipations = participationRepository
                .findByUserId((long) student.getUser().getId());

        // Récupérer les IDs des quiz déjà complétés
        List<Integer> completedQuizIds = completedParticipations.stream()
                .map(p -> p.getQuiz().getId())
                .collect(Collectors.toList());

        // Récupérer tous les quiz non complétés, triés par date de création (les plus récents en premier)
        List<Quiz> allQuizzes = quizRepository.findAll();

        List<Quiz> recommendedQuizzes = allQuizzes.stream()
                .filter(quiz -> !completedQuizIds.contains(quiz.getId()))
                .sorted(Comparator.comparing(Quiz::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        log.info("{} quiz recommandés trouvés", recommendedQuizzes.size());
        return recommendedQuizzes;
    }

    /**
     * Supprime un étudiant
     */
    public void deleteStudent(Integer studentId) {
        log.warn("Suppression de l'étudiant avec l'ID: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        studentRepository.delete(student);
        log.info("Étudiant supprimé avec succès: {}", studentId);
    }

    /**
     * Récupère tous les étudiants
     */
    public List<Student> getAllStudents() {
        log.debug("Récupération de tous les étudiants");
        return studentRepository.findAll();
    }

    /**
     * Compte le nombre total d'étudiants
     */
    public long countStudents() {
        long count = studentRepository.count();
        log.debug("Nombre total d'étudiants: {}", count);
        return count;
    }

    /**
     * Recherche des étudiants par nom
     */
    public List<Student> searchStudentsByName(String searchTerm) {
        log.info("Recherche d'étudiants avec le terme: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String normalizedTerm = searchTerm.trim().toLowerCase();

        List<Student> results = studentRepository.findAll().stream()
                .filter(student ->
                        student.getFirstName().toLowerCase().contains(normalizedTerm) ||
                                student.getLastName().toLowerCase().contains(normalizedTerm) ||
                                student.getUser().getUsername().toLowerCase().contains(normalizedTerm) ||
                                student.getUser().getEmail().toLowerCase().contains(normalizedTerm)
                )
                .collect(Collectors.toList());

        log.info("{} étudiants trouvés pour le terme '{}'", results.size(), searchTerm);
        return results;
    }

    /**
     * Vérifie si un étudiant existe
     */
    public boolean existsByEmail(String email) {
        try {
            getStudentByEmail(email);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Convertit une Participation en ParticipationDTO
     */
    private ParticipationDTO convertToParticipationDTO(Participation participation) {
        ParticipationDTO dto = new ParticipationDTO();
        dto.setId(participation.getId());
        dto.setQuizId(participation.getQuiz().getId());
        dto.setQuizTitle(participation.getQuiz().getTitle());
        dto.setScore(participation.getScore());
        dto.setCompletedAt(participation.getCreatedAt());
        dto.setDuration(participation.getQuiz().getDuration());

        // Calculer le nombre de questions et de réponses correctes
        if (participation.getQuiz().getQuestions() != null) {
            int questionCount = participation.getQuiz().getQuestions().size();
            dto.setQuestionCount(questionCount);

            // Calculer le nombre de réponses correctes basé sur le score
            if (participation.getScore() != null && questionCount > 0) {
                BigDecimal percentage = participation.getScore(); // Le score est déjà en pourcentage (0-100)
                int correctAnswers = percentage
                        .multiply(new BigDecimal(questionCount))
                        .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP)
                        .intValue();
                dto.setCorrectAnswers(correctAnswers);
            }
        }

        return dto;
    }

    /**
     * Convertit une Participation en QuizHistoryDTO
     */
    private QuizHistoryDTO convertToQuizHistoryDTO(Participation participation) {
        QuizHistoryDTO dto = new QuizHistoryDTO();
        dto.setParticipationId(participation.getId());
        dto.setQuizId(participation.getQuiz().getId());
        dto.setQuizTitle(participation.getQuiz().getTitle());
        dto.setQuizDescription(participation.getQuiz().getDescription());
        dto.setScore(participation.getScore());
        dto.setCompletedAt(participation.getCreatedAt());

        // Nom du professeur
        if (participation.getQuiz().getProfessor() != null) {
            String professorName = participation.getQuiz().getProfessor().getFirstName() + " " +
                    participation.getQuiz().getProfessor().getLastName();
            dto.setProfessorName(professorName);
        } else {
            dto.setProfessorName("Professeur inconnu");
        }

        // Calculer le rang (position dans le classement pour ce quiz)
        List<Participation> allParticipations = participationRepository
                .findByQuizId(participation.getQuiz().getId());

        long rank = allParticipations.stream()
                .filter(p -> p.getScore() != null && participation.getScore() != null)
                .filter(p -> p.getScore().compareTo(participation.getScore()) > 0)
                .count() + 1;

        dto.setRank((int) rank);
        dto.setTotalParticipants(allParticipations.size());

        return dto;
    }
}
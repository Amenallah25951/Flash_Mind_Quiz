package org.example.flashmindbackend.service;

import org.example.flashmindbackend.dto.StudentStatsDTO;
import org.example.flashmindbackend.dto.ParticipationDTO;
import org.example.flashmindbackend.dto.QuizHistoryDTO;
import org.example.flashmindbackend.entity.Participation;
import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.User;
import org.example.flashmindbackend.repository.ParticipationRepository;
import org.example.flashmindbackend.repository.StudentRepository;
import org.example.flashmindbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;

    /**
     * Récupère un étudiant par son email
     */
    public Student getStudentByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (user.getRole() != User.Role.student) {
            throw new RuntimeException("User is not a student");
        }

        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    /**
     * Récupère un étudiant par son ID utilisateur
     */
    public Student getStudentByUserId(Integer userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with user id: " + userId));
    }

    /**
     * Récupère les statistiques d'un étudiant
     */
    public StudentStatsDTO getStudentStats(String email) {
        Student student = getStudentByEmail(email);
        List<Participation> participations = participationRepository.findByUserId(student.getUser().getId());

        StudentStatsDTO stats = new StudentStatsDTO();

        // Nombre total de quiz complétés
        stats.setTotalQuizzes(participations.size());

        // Score moyen
        if (!participations.isEmpty()) {
            BigDecimal totalScore = participations.stream()
                    .map(Participation::getScore)
                    .filter(score -> score != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal averageScore = totalScore.divide(
                    new BigDecimal(participations.size()),
                    2,
                    RoundingMode.HALF_UP
            );
            stats.setAverageScore(averageScore);
        } else {
            stats.setAverageScore(BigDecimal.ZERO);
        }

        // Série actuelle (streak)
        stats.setCurrentStreak(calculateStreak(participations));

        // Meilleur score
        stats.setBestScore(calculateBestScore(participations));

        // Nombre de quiz parfaits (score = 100%)
        stats.setPerfectQuizzes(calculatePerfectQuizzes(participations));

        // Taux de réussite moyen (en pourcentage)
        stats.setSuccessRate(calculateSuccessRate(participations));

        return stats;
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
     * Calcule le taux de réussite moyen
     */
    private BigDecimal calculateSuccessRate(List<Participation> participations) {
        if (participations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = participations.stream()
                .map(Participation::getScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
        Student student = getStudentByEmail(email);
        List<Participation> participations = participationRepository.findByUserId(student.getUser().getId());

        return participations.stream()
                .sorted(Comparator.comparing(Participation::getCreatedAt).reversed())
                .map(this::convertToQuizHistoryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les détails d'une participation spécifique
     */
    public ParticipationDTO getParticipationDetails(String email, Integer participationId) {
        Student student = getStudentByEmail(email);
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        // Vérifier que la participation appartient bien à l'étudiant
        if (!participation.getUser().getId().equals(student.getUser().getId())) {
            throw new RuntimeException("Unauthorized access to participation");
        }

        return convertToParticipationDTO(participation);
    }

    /**
     * Met à jour le profil de l'étudiant
     */
    public Student updateStudentProfile(String email, String firstName, String lastName) {
        Student student = getStudentByEmail(email);

        if (firstName != null && !firstName.trim().isEmpty()) {
            student.setFirstName(firstName);
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            student.setLastName(lastName);
        }

        return studentRepository.save(student);
    }

    /**
     * Récupère le classement global des étudiants
     */
    public List<StudentStatsDTO> getGlobalLeaderboard(int limit) {
        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .map(student -> {
                    List<Participation> participations = participationRepository
                            .findByUserId(student.getUser().getId());

                    StudentStatsDTO stats = new StudentStatsDTO();
                    stats.setStudentName(student.getFirstName() + " " + student.getLastName());
                    stats.setUsername(student.getUser().getUsername());
                    stats.setTotalQuizzes(participations.size());

                    if (!participations.isEmpty()) {
                        BigDecimal totalScore = participations.stream()
                                .map(Participation::getScore)
                                .filter(score -> score != null)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal averageScore = totalScore.divide(
                                new BigDecimal(participations.size()),
                                2,
                                RoundingMode.HALF_UP
                        );
                        stats.setAverageScore(averageScore);
                    } else {
                        stats.setAverageScore(BigDecimal.ZERO);
                    }

                    return stats;
                })
                .sorted(Comparator.comparing(StudentStatsDTO::getAverageScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les quiz recommandés pour un étudiant
     */
    public List<QuizHistoryDTO> getRecommendedQuizzes(String email, int limit) {
        Student student = getStudentByEmail(email);
        List<Participation> completedQuizzes = participationRepository
                .findByUserId(student.getUser().getId());

        List<Integer> completedQuizIds = completedQuizzes.stream()
                .map(p -> p.getQuiz().getId())
                .collect(Collectors.toList());

        // Cette logique peut être améliorée avec un algorithme de recommandation
        // Pour l'instant, on retourne simplement des quiz non complétés

        return new ArrayList<>(); // À implémenter selon vos besoins
    }

    /**
     * Vérifie si un étudiant peut participer à un quiz
     */
    public boolean canParticipateInQuiz(String email, Integer quizId) {
        Student student = getStudentByEmail(email);

        // Vérifier si l'étudiant a déjà participé à ce quiz
        List<Participation> participations = participationRepository.findByUserId(student.getUser().getId());

        boolean alreadyParticipated = participations.stream()
                .anyMatch(p -> p.getQuiz().getId().equals(quizId));

        // Vous pouvez ajouter d'autres règles ici
        // Par exemple : limiter le nombre de tentatives, vérifier les dates, etc.

        return !alreadyParticipated;
    }

    /**
     * Enregistre une nouvelle participation
     */
    public Participation createParticipation(String email, Integer quizId, BigDecimal score) {
        Student student = getStudentByEmail(email);

        if (!canParticipateInQuiz(email, quizId)) {
            throw new RuntimeException("Student has already participated in this quiz");
        }

        Participation participation = new Participation();
        participation.setUser(student.getUser());
        participation.setScore(score);
        // Note: Il faut aussi définir le quiz, à faire dans le QuizService

        return participationRepository.save(participation);
    }

    /**
     * Convertit une Participation en ParticipationDTO
     */
    private ParticipationDTO convertToParticipationDTO(Participation participation) {
        ParticipationDTO dto = new ParticipationDTO();
        dto.setId(participation.getId());
        dto.setQuizTitle(participation.getQuiz().getTitle());
        dto.setQuizId(participation.getQuiz().getId());
        dto.setScore(participation.getScore());
        dto.setCompletedAt(participation.getCreatedAt());
        dto.setDuration(participation.getQuiz().getDuration());

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
        dto.setProfessorName(
                participation.getQuiz().getProfessor().getFirstName() + " " +
                        participation.getQuiz().getProfessor().getLastName()
        );

        // Calculer le rang (position dans le classement)
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

    /**
     * Supprime un étudiant
     */
    public void deleteStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        studentRepository.delete(student);
    }

    /**
     * Récupère tous les étudiants
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Compte le nombre total d'étudiants
     */
    public long countStudents() {
        return studentRepository.count();
    }

    /**
     * Recherche des étudiants par nom
     */
    public List<Student> searchStudentsByName(String searchTerm) {
        return studentRepository.findAll().stream()
                .filter(student ->
                        student.getFirstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                student.getLastName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                student.getUser().getUsername().toLowerCase().contains(searchTerm.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    /**
     * Récupère les étudiants les plus actifs
     */
    public List<StudentStatsDTO> getMostActiveStudents(int limit) {
        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .map(student -> {
                    List<Participation> participations = participationRepository
                            .findByUserId(student.getUser().getId());

                    StudentStatsDTO stats = new StudentStatsDTO();
                    stats.setStudentName(student.getFirstName() + " " + student.getLastName());
                    stats.setUsername(student.getUser().getUsername());
                    stats.setTotalQuizzes(participations.size());

                    return stats;
                })
                .sorted(Comparator.comparing(StudentStatsDTO::getTotalQuizzes).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
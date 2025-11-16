package org.example.flashmindbackend.service;

import org.example.flashmindbackend.dto.LoginRequest;
import org.example.flashmindbackend.dto.LoginResponse;
import org.example.flashmindbackend.dto.SignupRequest;
import org.example.flashmindbackend.entity.Professor;
import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.Users;
import org.example.flashmindbackend.repository.ProfessorRepository;
import org.example.flashmindbackend.repository.StudentRepository;
import org.example.flashmindbackend.repository.UserRepository;
import org.example.flashmindbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    @Transactional
    public void signup(SignupRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Tentative d'inscription avec un email existant: {}", request.getEmail());
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Vérifier si le username existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Tentative d'inscription avec un username existant: {}", request.getUsername());
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }

        // Valider le rôle
        String role = request.getRole().toLowerCase();
        if (!role.equals("student") && !role.equals("professor")) {
            throw new RuntimeException("Rôle invalide. Utilisez 'student' ou 'professor'");
        }

        try {
            // Créer l'utilisateur
            Users users = new Users();
            users.setUsername(request.getUsername());
            users.setEmail(request.getEmail());
            users.setPassword(passwordEncoder.encode(request.getPassword()));
            users.setRole(Users.Role.valueOf(role));
            users.setEnabled(false); // Le compte sera activé après vérification email
            users.setEmailVerified(false);

            // Générer un token de vérification
            String verificationToken = UUID.randomUUID().toString();
            users.setVerificationToken(verificationToken);
            users.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

            Users savedUsers = userRepository.save(users);
            log.info("Utilisateur créé avec succès: {}", savedUsers.getEmail());

            // Créer l'entité spécifique selon le rôle
            if ("student".equalsIgnoreCase(request.getRole())) {
                Student student = new Student();
                student.setUsers(savedUsers);
                student.setFirstName(request.getFirstName());
                student.setLastName(request.getLastName());
                studentRepository.save(student);
                log.info("Profil étudiant créé pour: {}", savedUsers.getEmail());
            } else if ("professor".equalsIgnoreCase(request.getRole())) {
                Professor professor = new Professor();
                professor.setUsers(savedUsers);
                professor.setFirstName(request.getFirstName());
                professor.setLastName(request.getLastName());
                professorRepository.save(professor);
                log.info("Profil professeur créé pour: {}", savedUsers.getEmail());
            }

            // Envoyer l'email de vérification
            try {
                emailService.sendVerificationEmail(
                        savedUsers.getEmail(),
                        savedUsers.getUsername(),
                        savedUsers.getVerificationToken()
                );
                log.info("Email de vérification envoyé à: {}", savedUsers.getEmail());
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email de vérification à {}: {}",
                        savedUsers.getEmail(), e.getMessage());
                // Supprimer l'utilisateur si l'email n'a pas pu être envoyé
                userRepository.delete(savedUsers);
                throw new RuntimeException("Erreur lors de l'envoi de l'email de vérification. Veuillez réessayer.");
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'inscription pour {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    @Transactional
    public void verifyEmail(String token) {
        log.info("Tentative de vérification d'email avec le token: {}...",
                token != null ? token.substring(0, Math.min(8, token.length())) : "null");

        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Le token de vérification est manquant");
        }

        Users users = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> {
                    log.warn("Token de vérification invalide: {}", token);
                    return new RuntimeException("Token de vérification invalide ou expiré");
                });

        // Vérifier si le token a expiré
        if (users.getVerificationTokenExpiry() != null &&
                users.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Token expiré pour l'utilisateur: {}", users.getEmail());
            throw new RuntimeException("Le token de vérification a expiré. Veuillez demander un nouveau lien de vérification.");
        }

        // Vérifier si l'email est déjà vérifié
        if (users.isEmailVerified()) {
            log.info("Email déjà vérifié pour: {}", users.getEmail());
            throw new RuntimeException("Cet email est déjà vérifié. Vous pouvez vous connecter.");
        }

        // Marquer l'email comme vérifié et activer le compte
        users.setEmailVerified(true);
        users.setEnabled(true);
        users.setVerificationToken(null);
        users.setVerificationTokenExpiry(null);
        userRepository.save(users);

        log.info("Email vérifié avec succès pour: {}", users.getEmail());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        log.info("Demande de renvoi d'email de vérification pour: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("L'adresse email est requise");
        }

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentative de renvoi d'email pour un utilisateur inexistant: {}", email);
                    return new RuntimeException("Aucun compte n'est associé à cette adresse email");
                });

        // Vérifier si l'email est déjà vérifié
        if (users.isEmailVerified()) {
            log.info("Tentative de renvoi pour un email déjà vérifié: {}", email);
            throw new RuntimeException("Cet email est déjà vérifié. Vous pouvez vous connecter.");
        }

        // Générer un nouveau token
        String verificationToken = UUID.randomUUID().toString();
        users.setVerificationToken(verificationToken);
        users.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(users);

        // Renvoyer l'email
        try {
            emailService.sendVerificationEmail(
                    users.getEmail(),
                    users.getUsername(),
                    verificationToken
            );
            log.info("Email de vérification renvoyé avec succès à: {}", email);
        } catch (Exception e) {
            log.error("Erreur lors du renvoi de l'email de vérification à {}: {}", email, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email. Veuillez réessayer plus tard.");
        }
    }

    public boolean isEmailVerified(String email) {
        log.debug("Vérification du statut de l'email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("L'adresse email est requise");
        }

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte n'est associé à cette adresse email"));

        return users.isEmailVerified();
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour: {}", request.getEmail());

        try {
            // Authentifier l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Échec d'authentification pour: {}", request.getEmail());
            throw new RuntimeException("Email ou mot de passe incorrect");
        } catch (Exception e) {
            log.error("Erreur d'authentification pour {}: {}", request.getEmail(), e.getMessage());
            throw new RuntimeException("Erreur lors de l'authentification");
        }

        // Récupérer l'utilisateur
        Users users = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si le compte est activé
        if (!users.isEnabled()) {
            log.warn("Tentative de connexion avec un compte désactivé: {}", request.getEmail());
            throw new RuntimeException("Votre compte est désactivé. Veuillez contacter l'administrateur.");
        }

        // Vérifier si l'email est vérifié
        if (!users.isEmailVerified()) {
            log.warn("Tentative de connexion avec un email non vérifié: {}", request.getEmail());
            throw new RuntimeException("Votre email n'a pas encore été vérifié. Veuillez vérifier votre boîte de réception.");
        }

        // Générer le token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Sauvegarder le refresh token
        users.setRefreshToken(refreshToken);
        userRepository.save(users);

        // Récupérer les informations complémentaires selon le rôle
        String firstName = "";
        String lastName = "";

        if (users.getRole() == Users.Role.student) {
            Student student = studentRepository.findByUser(users)
                    .orElseThrow(() -> new RuntimeException("Profil étudiant non trouvé"));
            firstName = student.getFirstName();
            lastName = student.getLastName();
        } else if (users.getRole() == Users.Role.professor) {
            Professor professor = professorRepository.findByUser(users)
                    .orElseThrow(() -> new RuntimeException("Profil professeur non trouvé"));
            firstName = professor.getFirstName();
            lastName = professor.getLastName();
        }

        log.info("Connexion réussie pour: {}", users.getEmail());

        return new LoginResponse(
                token,
                refreshToken,
                users.getUsername(),
                users.getEmail(),
                users.getRole().name(),
                firstName,
                lastName
        );
    }

    @Transactional
    public void logout(String email) {
        log.info("Déconnexion de l'utilisateur: {}", email);

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        users.setRefreshToken(null);
        userRepository.save(users);

        log.info("Utilisateur déconnecté: {}", email);
    }

    public LoginResponse refreshToken(String refreshToken) {
        log.debug("Tentative de rafraîchissement du token");

        // Valider le refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token invalide");
        }

        String email = jwtUtil.extractUsername(refreshToken);
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que le refresh token correspond à celui en base
        if (!refreshToken.equals(users.getRefreshToken())) {
            throw new RuntimeException("Refresh token invalide");
        }

        // Générer de nouveaux tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String newToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Mettre à jour le refresh token en base
        users.setRefreshToken(newRefreshToken);
        userRepository.save(users);

        // Récupérer les informations de profil
        String firstName = "";
        String lastName = "";

        if (users.getRole() == Users.Role.student) {
            Student student = studentRepository.findByUser(users)
                    .orElseThrow(() -> new RuntimeException("Profil étudiant non trouvé"));
            firstName = student.getFirstName();
            lastName = student.getLastName();
        } else if (users.getRole() == Users.Role.professor) {
            Professor professor = professorRepository.findByUser(users)
                    .orElseThrow(() -> new RuntimeException("Profil professeur non trouvé"));
            firstName = professor.getFirstName();
            lastName = professor.getLastName();
        }

        log.info("Token rafraîchi avec succès pour: {}", email);

        return new LoginResponse(
                newToken,
                newRefreshToken,
                users.getUsername(),
                users.getEmail(),
                users.getRole().name(),
                firstName,
                lastName
        );
    }
    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Demande de réinitialisation de mot de passe pour: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("L'adresse email est requise");
        }

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentative de réinitialisation pour un email inexistant: {}", email);
                    return new RuntimeException("Si cet email existe, un lien de réinitialisation a été envoyé");
                });

        // Générer un token de réinitialisation
        String resetToken = UUID.randomUUID().toString();
        users.setPasswordResetToken(resetToken);
        users.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));

        Users savedUsers = userRepository.save(users);

        // LOG IMPORTANT pour debug
        log.info("🔐 Token généré pour {}: {}", email, resetToken);
        log.info("⏰ Expire à: {}", savedUsers.getPasswordResetTokenExpiry());

        // Envoyer l'email
        try {
            emailService.sendPasswordResetEmail(
                    users.getEmail(),
                    users.getUsername(),
                    resetToken
            );
            log.info("✅ Email de réinitialisation envoyé à: {}", email);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de réinitialisation à {}: {}", email, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email. Veuillez réessayer plus tard.");
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.info("Tentative de réinitialisation de mot de passe avec le token: {}...",
                token != null ? token.substring(0, Math.min(8, token.length())) : "null");

        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Le token de réinitialisation est manquant");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Le nouveau mot de passe est requis");
        }

        // Valider la longueur du mot de passe
        if (newPassword.length() < 8) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 8 caractères");
        }

        Users users = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> {
                    log.warn("Token de réinitialisation invalide: {}", token);
                    return new RuntimeException("Token de réinitialisation invalide ou expiré");
                });

        // Vérifier si le token a expiré
        if (users.getPasswordResetTokenExpiry() != null &&
                users.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Token expiré pour l'utilisateur: {}", users.getEmail());
            throw new RuntimeException("Le token de réinitialisation a expiré. Veuillez refaire une demande.");
        }

        // Mettre à jour le mot de passe
        users.setPassword(passwordEncoder.encode(newPassword));
        users.setPasswordResetToken(null);
        users.setPasswordResetTokenExpiry(null);

        // Invalider tous les refresh tokens pour forcer une nouvelle connexion
        users.setRefreshToken(null);

        userRepository.save(users);

        log.info("Mot de passe réinitialisé avec succès pour: {}", users.getEmail());
    }

    @Transactional
    public void validateResetToken(String token) {
        log.debug("Validation du token de réinitialisation");

        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Le token de réinitialisation est manquant");
        }

        Users users = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token de réinitialisation invalide"));

        // Vérifier si le token a expiré
        if (users.getPasswordResetTokenExpiry() != null &&
                users.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le token de réinitialisation a expiré");
        }

        log.debug("Token valide pour l'utilisateur: {}", users.getEmail());
    }
}
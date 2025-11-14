package org.example.flashmindbackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flashmindbackend.dto.*;
import org.example.flashmindbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowedHeaders = "*", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.email.verification.enabled:false}")
    private boolean emailVerificationEnabled;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            log.info("📝 Requête d'inscription reçue pour: {}", request.getEmail());

            authService.signup(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", request.getEmail());

            if (emailVerificationEnabled) {
                response.put("message", "Compte créé avec succès. Veuillez vérifier votre email pour activer votre compte.");
                response.put("requiresEmailVerification", true);
            } else {
                response.put("message", "Compte créé avec succès. Vous pouvez maintenant vous connecter.");
                response.put("requiresEmailVerification", false);
            }

            log.info("✅ Inscription réussie pour: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'inscription pour {}: {}", request.getEmail(), e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("🔐 Tentative de connexion pour: {}", request.getEmail());

            LoginResponse response = authService.login(request);

            log.info("✅ Connexion réussie pour: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.warn("❌ Échec de connexion pour {}: {}", request.getEmail(), e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Vérification de l'email via token
     * Redirige vers le frontend avec le résultat
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        // CORRECTION: Vérifier si le token n'est pas vide avant substring
        String tokenPreview = (token != null && token.length() >= 8)
                ? token.substring(0, 8) + "..."
                : "invalid";

        log.info("📧 Tentative de vérification d'email avec token: {}", tokenPreview);

        String baseRedirectPath = frontendUrl + "/email-verified";

        try {
            // Vérifier l'email
            authService.verifyEmail(token);

            // Redirection vers le frontend avec succès
            String successUrl = baseRedirectPath + "?success=true&message=" +
                    URLEncoder.encode("Votre email a été vérifié avec succès ! Vous pouvez maintenant vous connecter.", StandardCharsets.UTF_8);

            log.info("✅ Vérification d'email réussie, redirection vers: {}", frontendUrl);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, successUrl)
                    .build();

        } catch (Exception e) {
            // Redirection vers le frontend avec erreur
            String errorMessage = e.getMessage();
            String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            String errorUrl = baseRedirectPath + "?success=false&error=" + encodedError;

            log.error("❌ Échec de vérification d'email: {}", errorMessage);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, errorUrl)
                    .build();
        }
    }

    /**
     * Renvoi de l'email de vérification
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");

            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "L'adresse email est requise");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            log.info("🔄 Demande de renvoi d'email de vérification pour: {}", email);

            authService.resendVerificationEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Email de vérification renvoyé avec succès. Veuillez vérifier votre boîte de réception.");
            response.put("email", email);

            log.info("✅ Email de vérification renvoyé avec succès à: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors du renvoi d'email: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Vérification du statut de l'email
     */
    @GetMapping("/check-email-verified")
    public ResponseEntity<?> checkEmailVerified(@RequestParam String email) {
        try {
            log.debug("🔍 Vérification du statut de l'email pour: {}", email);

            boolean isVerified = authService.isEmailVerified(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", email);
            response.put("verified", isVerified);
            response.put("emailVerificationEnabled", emailVerificationEnabled);

            log.debug("📧 Statut de vérification pour {}: {}", email, isVerified);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification du statut email pour {}: {}", email, e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Déconnexion de l'utilisateur
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");

            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "L'adresse email est requise");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            log.info("🚪 Déconnexion demandée pour: {}", email);

            authService.logout(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Déconnexion réussie");

            log.info("✅ Déconnexion réussie pour: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la déconnexion: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Rafraîchir le token d'accès
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> requestBody) {
        try {
            String refreshToken = requestBody.get("refreshToken");

            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Refresh token manquant");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            log.info("🔄 Demande de rafraîchissement de token");

            LoginResponse response = authService.refreshToken(refreshToken);

            log.info("✅ Token rafraîchi avec succès");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors du rafraîchissement du token: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Endpoint de santé
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auth-service");
        response.put("emailVerificationEnabled", emailVerificationEnabled);
        response.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir la configuration publique
     */
    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("emailVerificationEnabled", emailVerificationEnabled);
        config.put("passwordMinLength", 8);
        config.put("passwordRequiresUppercase", true);
        config.put("passwordRequiresNumber", true);
        config.put("passwordRequiresSpecialChar", true);

        return ResponseEntity.ok(config);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "message", "Si cet email existe, un lien de réinitialisation a été envoyé",
                    "success", true
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la demande de réinitialisation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "success", false
                    ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());

            log.info("✅ Mot de passe réinitialisé avec succès");

            // Retourner une réponse JSON au lieu d'une redirection
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Votre mot de passe a été réinitialisé avec succès");
            response.put("redirectUrl", frontendUrl + "/login?message=" +
                    URLEncoder.encode("Votre mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.", StandardCharsets.UTF_8));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Erreur lors de la réinitialisation du mot de passe: {}", e.getMessage());

            // Retourner une erreur JSON au lieu d'une redirection
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("redirectUrl", frontendUrl + "/reset-password?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8) + "&token=" + request.getToken());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        try {
            authService.validateResetToken(token);
            return ResponseEntity.ok(Map.of(
                    "message", "Token valide",
                    "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "success", false
                    ));
        }
    }


    /**
     * Gestion globale des erreurs
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("💥 Erreur non gérée dans AuthController: {}", e.getMessage(), e);

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", "Une erreur interne est survenue");
        error.put("message", "Veuillez réessayer plus tard ou contacter le support");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
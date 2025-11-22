package org.example.flashmindbackend.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendVerificationEmail(String toEmail, String username, String token) {
        try {
            String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;
            String htmlContent = buildModernEmailTemplate(username, verificationLink);

            sendEmail(toEmail, "✨ Vérifiez votre compte FlashMind", htmlContent);

            log.info("✅ Email de vérification envoyé à: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de vérification à {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de vérification", e);
        }
    }
    @PostConstruct
    public void debugBrevo() {
        System.out.println("🔑 BREVO LOADED = " + brevoApiKey);
    }

    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String htmlContent = buildPasswordResetTemplate(username, resetLink);

            sendEmail(toEmail, "🔐 Réinitialisation de votre mot de passe FlashMind", htmlContent);

            log.info("✅ Email de réinitialisation envoyé à: {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de réinitialisation à {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de réinitialisation", e);
        }
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        String url = "https://api.brevo.com/v3/smtp/email";

        Map<String, Object> email = new HashMap<>();
        email.put("sender", Map.of(
                "name", "FlashMind",
                "email", "flashmindquizz@gmail.com"
        ));
        email.put("to", List.of(Map.of(
                "email", toEmail,
                "name", toEmail
        )));
        email.put("subject", subject);
        email.put("htmlContent", htmlContent);

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(email, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("📧 Email envoyé avec succès - Status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("❌ Erreur API Brevo: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    private String buildModernEmailTemplate(String username, String verificationLink) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 20px; }" +
                ".email-wrapper { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 50px 30px; text-align: center; position: relative; overflow: hidden; }" +
                ".header::before { content: ''; position: absolute; top: -50%; left: -50%; width: 200%; height: 200%; background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%); animation: pulse 4s ease-in-out infinite; }" +
                "@keyframes pulse { 0%, 100% { transform: scale(1); opacity: 0.5; } 50% { transform: scale(1.1); opacity: 0.8; } }" +
                ".logo { width: 80px; height: 80px; margin: 0 auto 20px; background: white; border-radius: 20px; display: flex; align-items: center; justify-content: center; font-size: 40px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); position: relative; z-index: 1; }" +
                ".header h1 { color: white; font-size: 32px; font-weight: 700; margin-bottom: 10px; position: relative; z-index: 1; text-shadow: 0 2px 10px rgba(0,0,0,0.2); }" +
                ".header p { color: rgba(255,255,255,0.9); font-size: 16px; position: relative; z-index: 1; }" +
                ".content { padding: 50px 40px; }" +
                ".greeting { font-size: 24px; color: #2d3748; margin-bottom: 20px; font-weight: 600; }" +
                ".message { font-size: 16px; color: #4a5568; line-height: 1.8; margin-bottom: 30px; }" +
                ".button-container { text-align: center; margin: 40px 0; }" +
                ".verify-button { display: inline-block; padding: 18px 50px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: 600; box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4); transition: all 0.3s ease; }" +
                ".verify-button:hover { transform: translateY(-2px); box-shadow: 0 15px 40px rgba(102, 126, 234, 0.6); }" +
                ".divider { height: 1px; background: linear-gradient(90deg, transparent, #e2e8f0, transparent); margin: 30px 0; }" +
                ".info-box { background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%); border-left: 4px solid #667eea; padding: 20px; border-radius: 10px; margin: 20px 0; }" +
                ".info-box p { color: #4a5568; font-size: 14px; margin: 0; line-height: 1.6; }" +
                ".link-box { background: #f7fafc; border: 2px dashed #cbd5e0; border-radius: 10px; padding: 15px; margin: 20px 0; text-align: center; }" +
                ".link-box a { color: #667eea; word-break: break-all; font-size: 13px; text-decoration: none; }" +
                ".features { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin: 30px 0; }" +
                ".feature { text-align: center; padding: 20px; background: #f7fafc; border-radius: 15px; transition: transform 0.3s ease; }" +
                ".feature:hover { transform: translateY(-5px); }" +
                ".feature-icon { font-size: 40px; margin-bottom: 10px; }" +
                ".feature-text { color: #4a5568; font-size: 13px; font-weight: 600; }" +
                ".footer { background: #2d3748; padding: 30px; text-align: center; color: #a0aec0; }" +
                ".footer-links { margin: 20px 0; }" +
                ".footer-links a { color: #667eea; text-decoration: none; margin: 0 15px; font-size: 14px; }" +
                ".footer-links a:hover { color: #764ba2; }" +
                ".social-icons { margin: 20px 0; }" +
                ".social-icons a { display: inline-block; width: 40px; height: 40px; background: #4a5568; border-radius: 50%; margin: 0 5px; line-height: 40px; text-decoration: none; color: white; font-size: 18px; transition: all 0.3s ease; }" +
                ".social-icons a:hover { background: #667eea; transform: translateY(-3px); }" +
                ".copyright { font-size: 12px; color: #718096; margin-top: 20px; }" +
                ".timer { background: #fff3cd; border: 2px solid #ffc107; border-radius: 10px; padding: 15px; margin: 20px 0; text-align: center; }" +
                ".timer-icon { font-size: 30px; margin-bottom: 5px; }" +
                ".timer-text { color: #856404; font-weight: 600; margin: 0; }" +
                "@media only screen and (max-width: 600px) {" +
                "  body { padding: 20px 10px; }" +
                "  .content { padding: 30px 20px; }" +
                "  .header h1 { font-size: 24px; }" +
                "  .features { grid-template-columns: 1fr; }" +
                "  .verify-button { padding: 15px 40px; font-size: 16px; }" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-wrapper'>" +
                "<div class='header'>" +
                "<div class='logo'>🧠</div>" +
                "<h1>FlashMind</h1>" +
                "<p>Votre plateforme d'apprentissage intelligente</p>" +
                "</div>" +
                "<div class='content'>" +
                "<div class='greeting'>Bonjour " + username + " ! 👋</div>" +
                "<p class='message'>" +
                "Nous sommes <strong>ravis</strong> de vous accueillir sur FlashMind ! 🎉<br><br>" +
                "Votre compte a été créé avec succès. Pour commencer votre voyage d'apprentissage et accéder à toutes nos fonctionnalités, " +
                "il ne vous reste plus qu'à vérifier votre adresse email." +
                "</p>" +
                "<div class='features'>" +
                "<div class='feature'>" +
                "<div class='feature-icon'>📚</div>" +
                "<div class='feature-text'>Flashcards Intelligentes</div>" +
                "</div>" +
                "<div class='feature'>" +
                "<div class='feature-icon'>🎯</div>" +
                "<div class='feature-text'>Suivi de Progression</div>" +
                "</div>" +
                "<div class='feature'>" +
                "<div class='feature-icon'>🤝</div>" +
                "<div class='feature-text'>Collaboration</div>" +
                "</div>" +
                "</div>" +
                "<div class='button-container'>" +
                "<a href='" + verificationLink + "' class='verify-button'>✨ Vérifier mon email</a>" +
                "</div>" +
                "<div class='timer'>" +
                "<div class='timer-icon'>⏰</div>" +
                "<p class='timer-text'>Ce lien expire dans 24 heures</p>" +
                "</div>" +
                "<div class='divider'></div>" +
                "<p style='text-align: center; color: #718096; font-size: 14px; margin-bottom: 10px;'>" +
                "Le bouton ne fonctionne pas ? Copiez ce lien dans votre navigateur :" +
                "</p>" +
                "<div class='link-box'>" +
                "<a href='" + verificationLink + "'>" + verificationLink + "</a>" +
                "</div>" +
                "<div class='info-box'>" +
                "<p><strong>🔒 Sécurité :</strong> Si vous n'avez pas créé de compte sur FlashMind, " +
                "vous pouvez ignorer cet email en toute sécurité. Votre adresse ne sera pas utilisée sans votre consentement.</p>" +
                "</div>" +
                "<div class='divider'></div>" +
                "<p style='text-align: center; color: #718096; font-size: 14px;'>" +
                "Besoin d'aide ? Notre équipe est là pour vous ! Contactez-nous à tout moment." +
                "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<div class='social-icons'>" +
                "<a href='#'>📘</a>" +
                "<a href='#'>🐦</a>" +
                "<a href='#'>📷</a>" +
                "<a href='#'>💼</a>" +
                "</div>" +
                "<div class='footer-links'>" +
                "<a href='#'>Centre d'aide</a> •" +
                "<a href='#'>Conditions d'utilisation</a> •" +
                "<a href='#'>Confidentialité</a>" +
                "</div>" +
                "<div class='copyright'>" +
                "© 2025 FlashMind. Tous droits réservés.<br>" +
                "FlashMind - Apprenez plus intelligemment, pas plus dur. 🚀" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordResetTemplate(String username, String resetLink) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); padding: 40px 20px; }" +
                ".email-wrapper { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }" +
                ".header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); padding: 50px 30px; text-align: center; position: relative; }" +
                ".logo { width: 80px; height: 80px; margin: 0 auto 20px; background: white; border-radius: 20px; display: flex; align-items: center; justify-content: center; font-size: 40px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }" +
                ".header h1 { color: white; font-size: 32px; font-weight: 700; margin-bottom: 10px; text-shadow: 0 2px 10px rgba(0,0,0,0.2); }" +
                ".header p { color: rgba(255,255,255,0.9); font-size: 16px; }" +
                ".content { padding: 50px 40px; }" +
                ".alert-box { background: linear-gradient(135deg, #fff5f5 0%, #fed7d7 100%); border-left: 4px solid #f5576c; padding: 20px; border-radius: 10px; margin: 20px 0; }" +
                ".alert-icon { font-size: 40px; text-align: center; margin-bottom: 10px; }" +
                ".greeting { font-size: 24px; color: #2d3748; margin-bottom: 20px; font-weight: 600; }" +
                ".message { font-size: 16px; color: #4a5568; line-height: 1.8; margin-bottom: 30px; }" +
                ".button-container { text-align: center; margin: 40px 0; }" +
                ".reset-button { display: inline-block; padding: 18px 50px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; text-decoration: none; border-radius: 50px; font-size: 18px; font-weight: 600; box-shadow: 0 10px 30px rgba(245, 87, 108, 0.4); transition: all 0.3s ease; }" +
                ".reset-button:hover { transform: translateY(-2px); box-shadow: 0 15px 40px rgba(245, 87, 108, 0.6); }" +
                ".timer { background: #fff3cd; border: 2px solid #ffc107; border-radius: 10px; padding: 15px; margin: 20px 0; text-align: center; }" +
                ".timer-icon { font-size: 30px; margin-bottom: 5px; }" +
                ".timer-text { color: #856404; font-weight: 600; margin: 0; }" +
                ".security-tips { background: #f7fafc; border-radius: 15px; padding: 25px; margin: 30px 0; }" +
                ".security-tips h3 { color: #2d3748; margin-bottom: 15px; font-size: 18px; }" +
                ".security-tips ul { list-style: none; padding: 0; }" +
                ".security-tips li { padding: 10px 0; color: #4a5568; font-size: 14px; padding-left: 30px; position: relative; }" +
                ".security-tips li:before { content: '🔒'; position: absolute; left: 0; }" +
                ".link-box { background: #f7fafc; border: 2px dashed #cbd5e0; border-radius: 10px; padding: 15px; margin: 20px 0; text-align: center; }" +
                ".link-box a { color: #f5576c; word-break: break-all; font-size: 13px; text-decoration: none; }" +
                ".divider { height: 1px; background: linear-gradient(90deg, transparent, #e2e8f0, transparent); margin: 30px 0; }" +
                ".footer { background: #2d3748; padding: 30px; text-align: center; color: #a0aec0; }" +
                ".copyright { font-size: 12px; color: #718096; margin-top: 20px; }" +
                "@media only screen and (max-width: 600px) {" +
                "  body { padding: 20px 10px; }" +
                "  .content { padding: 30px 20px; }" +
                "  .header h1 { font-size: 24px; }" +
                "  .reset-button { padding: 15px 40px; font-size: 16px; }" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-wrapper'>" +
                "<div class='header'>" +
                "<div class='logo'>🔐</div>" +
                "<h1>Réinitialisation du mot de passe</h1>" +
                "<p>FlashMind - Sécurité de votre compte</p>" +
                "</div>" +
                "<div class='content'>" +
                "<div class='alert-box'>" +
                "<div class='alert-icon'>⚠️</div>" +
                "<p style='color: #c53030; font-weight: 600; text-align: center; margin: 0;'>" +
                "Demande de réinitialisation de mot de passe détectée" +
                "</p>" +
                "</div>" +
                "<div class='greeting'>Bonjour " + username + ",</div>" +
                "<p class='message'>" +
                "Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte FlashMind. " +
                "Si vous êtes à l'origine de cette demande, cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe." +
                "</p>" +
                "<div class='button-container'>" +
                "<a href='" + resetLink + "' class='reset-button'>🔑 Réinitialiser mon mot de passe</a>" +
                "</div>" +
                "<div class='timer'>" +
                "<div class='timer-icon'>⏰</div>" +
                "<p class='timer-text'>Ce lien expire dans 1 heure pour votre sécurité</p>" +
                "</div>" +
                "<div class='security-tips'>" +
                "<h3>💡 Conseils de sécurité</h3>" +
                "<ul>" +
                "<li>Choisissez un mot de passe fort avec au moins 8 caractères</li>" +
                "<li>Mélangez lettres majuscules, minuscules, chiffres et symboles</li>" +
                "<li>N'utilisez pas le même mot de passe sur plusieurs sites</li>" +
                "<li>Ne partagez jamais votre mot de passe avec quiconque</li>" +
                "</ul>" +
                "</div>" +
                "<div class='divider'></div>" +
                "<p style='text-align: center; color: #718096; font-size: 14px; margin-bottom: 10px;'>" +
                "Le bouton ne fonctionne pas ? Copiez ce lien dans votre navigateur :" +
                "</p>" +
                "<div class='link-box'>" +
                "<a href='" + resetLink + "'>" + resetLink + "</a>" +
                "</div>" +
                "<div class='divider'></div>" +
                "<p style='background: #fef5e7; border-left: 4px solid #f39c12; padding: 15px; border-radius: 10px; color: #856404; font-size: 14px;'>" +
                "<strong>🚨 Vous n'avez pas demandé cette réinitialisation ?</strong><br>" +
                "Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer cet email et " +
                "contacter immédiatement notre support. Votre mot de passe actuel reste inchangé." +
                "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p style='margin-bottom: 15px;'>Besoin d'aide ? Contactez notre support</p>" +
                "<div class='copyright'>" +
                "© 2025 FlashMind. Tous droits réservés.<br>" +
                "Cet email a été envoyé pour des raisons de sécurité." +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
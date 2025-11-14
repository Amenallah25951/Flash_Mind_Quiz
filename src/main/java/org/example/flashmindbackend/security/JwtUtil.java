package org.example.flashmindbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:86400000}") // 24 heures par défaut (en millisecondes)
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 jours par défaut (en millisecondes)
    private Long refreshExpiration;

    /**
     * Générer un token d'accès (24h)
     * Inclut les rôles et informations de base de l'utilisateur
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Ajouter les rôles de l'utilisateur dans les claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        claims.put("type", "access");

        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    /**
     * Générer un refresh token (7 jours)
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    /**
     * Créer un token avec des claims personnalisés et une durée d'expiration
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtenir la clé de signature sécurisée
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Extraire le username (email) du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extraire la date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extraire la date d'émission du token
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Extraire un claim spécifique du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extraire tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expiré: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformé: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.error("Signature JWT invalide: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT non supporté: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Claims JWT vides: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Vérifier si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Valider le token avec les UserDetails
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Erreur lors de la validation du token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valider uniquement le token (sans UserDetails)
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifier si c'est un refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            log.warn("Impossible de vérifier le type de token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifier si c'est un access token
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "access".equals(claims.get("type"));
        } catch (Exception e) {
            log.warn("Impossible de vérifier le type de token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extraire les rôles du token
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (java.util.List<String>) claims.get("roles");
        } catch (Exception e) {
            log.warn("Impossible d'extraire les rôles du token: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Obtenir la durée d'expiration du token en millisecondes
     */
    public Long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Obtenir la durée d'expiration du refresh token en millisecondes
     */
    public Long getRefreshExpirationTime() {
        return refreshExpiration;
    }

    /**
     * Obtenir le temps restant avant expiration (en secondes)
     */
    public long getTimeUntilExpiration(String token) {
        try {
            Date expiration = extractExpiration(token);
            long timeRemaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, timeRemaining / 1000); // Retourne en secondes
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Vérifier si le token expire bientôt (dans les 5 prochaines minutes)
     */
    public boolean isTokenExpiringSoon(String token) {
        long timeRemaining = getTimeUntilExpiration(token);
        return timeRemaining > 0 && timeRemaining < 300; // 5 minutes en secondes
    }

    /**
     * Extraire toutes les informations du token (pour le debug)
     */
    public Map<String, Object> getTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> info = new HashMap<>();
            info.put("subject", claims.getSubject());
            info.put("issuedAt", claims.getIssuedAt());
            info.put("expiration", claims.getExpiration());
            info.put("type", claims.get("type"));
            info.put("roles", claims.get("roles"));
            info.put("isExpired", isTokenExpired(token));
            info.put("timeUntilExpiration", getTimeUntilExpiration(token));
            return info;
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction des infos du token: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
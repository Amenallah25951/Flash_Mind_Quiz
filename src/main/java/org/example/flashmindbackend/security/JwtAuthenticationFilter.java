package org.example.flashmindbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // LOGS DE DEBUG
        System.out.println("=== JWT FILTER DEBUG ===");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Authorization Header: " + (authorizationHeader != null ? authorizationHeader.substring(0, 20) + "..." : "NULL"));

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = jwtUtil.extractUsername(jwt);
            System.out.println("✅ JWT extrait");
            System.out.println("📧 Email extrait: " + email);
        } else {
            System.out.println("❌ Pas de Bearer token trouvé");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            System.out.println("👤 UserDetails chargé pour: " + userDetails.getUsername());
            System.out.println("🔐 Authorities: " + userDetails.getAuthorities());

            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("✅ Token valide");

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                System.out.println("✅ Authentication définie dans SecurityContext");
                System.out.println("🔐 Authorities dans le token: " + authenticationToken.getAuthorities());
            } else {
                System.out.println("❌ Token invalide");
            }
        }

        System.out.println("========================");

        filterChain.doFilter(request, response);
    }
}
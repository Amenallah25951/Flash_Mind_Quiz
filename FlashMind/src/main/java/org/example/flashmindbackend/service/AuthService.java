package org.example.flashmindbackend.service;

import org.example.flashmindbackend.dto.LoginRequest;
import org.example.flashmindbackend.dto.LoginResponse;
import org.example.flashmindbackend.dto.SignupRequest;
import org.example.flashmindbackend.entity.Professor;
import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.User;
import org.example.flashmindbackend.repository.ProfessorRepository;
import org.example.flashmindbackend.repository.StudentRepository;
import org.example.flashmindbackend.repository.UserRepository;
import org.example.flashmindbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void signup(SignupRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Vérifier si le username existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Créer l'utilisateur
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole().toLowerCase()));

        User savedUser = userRepository.save(user);

        // Créer l'entité spécifique selon le rôle
        if ("student".equalsIgnoreCase(request.getRole())) {

            Student student = new Student();
            student.setUser(savedUser);
            student.setFirstName(request.getFirstName());
            student.setLastName(request.getLastName());
            studentRepository.save(student);
        } else if ("professor".equalsIgnoreCase(request.getRole())) {
            Professor professor = new Professor();
            professor.setUser(savedUser);
            professor.setFirstName(request.getFirstName());
            professor.setLastName(request.getLastName());
            professorRepository.save(professor);
        }
    }
    public LoginResponse login(LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Générer le token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateToken(userDetails); // À améliorer avec une logique différente

        // Sauvegarder le refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Récupérer les informations complémentaires selon le rôle
        String firstName = "";
        String lastName = "";

        if (user.getRole() == User.Role.student) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            firstName = student.getFirstName();
            lastName = student.getLastName();
        } else if (user.getRole() == User.Role.professor) {
            Professor professor = professorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Professor not found"));
            firstName = professor.getFirstName();
            lastName = professor.getLastName();
        }

        return new LoginResponse(
                token,
                refreshToken,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                firstName,
                lastName
        );
    }
}

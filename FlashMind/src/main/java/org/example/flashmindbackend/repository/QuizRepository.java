package org.example.flashmindbackend.repository;

import org.example.flashmindbackend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Optional<Quiz> findByCode(String code);
    List<Quiz> findByProfessorId(Integer professorId);
}
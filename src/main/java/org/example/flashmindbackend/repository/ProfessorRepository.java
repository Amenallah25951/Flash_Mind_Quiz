package org.example.flashmindbackend.repository;

import org.example.flashmindbackend.entity.Professor;
import org.example.flashmindbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    Optional<Professor> findByUser(User user);
    Optional<Professor> findByUserId(Integer userId);
}
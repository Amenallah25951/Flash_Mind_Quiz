package org.example.flashmindbackend.repository;



import org.example.flashmindbackend.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    List<Participation> findByUserId(Long userId);
    List<Participation> findByQuizId(Integer quizId);

     Optional<Participation> findById(Integer participationId);
}

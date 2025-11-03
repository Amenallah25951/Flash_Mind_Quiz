package org.example.flashmindbackend.repository;



import org.example.flashmindbackend.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    List<Participation> findByUserId(Integer userId);
    List<Participation> findByQuizId(Integer quizId);
}

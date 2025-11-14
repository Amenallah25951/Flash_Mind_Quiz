package org.example.flashmindbackend.repository;


import org.example.flashmindbackend.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Integer> {
    List<Response> findByQuestionId(Integer questionId);
}
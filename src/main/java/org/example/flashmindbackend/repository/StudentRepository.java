package org.example.flashmindbackend.repository;

import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserId(Integer userId);

}
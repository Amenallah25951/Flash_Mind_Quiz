package org.example.flashmindbackend.repository;

import org.example.flashmindbackend.entity.Student;
import org.example.flashmindbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUser(Users users);
    Optional<Student> findByUserId(Integer userId);

}
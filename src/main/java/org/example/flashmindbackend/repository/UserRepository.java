package org.example.flashmindbackend.repository;

import org.example.flashmindbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<Users> findByVerificationToken(String token);
    Optional<Users> findByPasswordResetToken(String passwordResetToken);

    // LOG pour debug
    @Query("SELECT u FROM Users u WHERE u.passwordResetToken = :token")
    Optional<Users> findByPasswordResetTokenWithLog(@Param("token") String token);

}
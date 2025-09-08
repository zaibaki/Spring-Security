package com.example.authbackend.repository;

import com.example.authbackend.entity.EmailVerificationToken;
import com.example.authbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    Optional<EmailVerificationToken> findByToken(String token);
    
    Optional<EmailVerificationToken> findByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken e WHERE e.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    void deleteByUser(User user);
}
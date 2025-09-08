package com.example.authbackend.repository;

import com.example.authbackend.entity.User;
import com.example.authbackend.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    Optional<User> findByEmailAndProvider(String email, AuthProvider provider);
}
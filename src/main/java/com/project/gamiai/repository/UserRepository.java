package com.project.gamiai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmailOrUsername(String email, String username);
    Optional<User> findByRefreshToken(String refreshToken);
}
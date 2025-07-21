package com.project.gamiai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.UserProgress;

public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {
    Optional<UserProgress> findByUserIdAndProblemId(Integer userId, Integer problemId);
}
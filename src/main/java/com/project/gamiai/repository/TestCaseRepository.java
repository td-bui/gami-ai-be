package com.project.gamiai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.TestCase;

public interface TestCaseRepository extends JpaRepository<TestCase, Integer> {
    List<TestCase> findByProblemId(Integer problemId);
    List<TestCase> findByProblemIdAndIsPublicTrue(Integer problemId);
}
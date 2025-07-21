package com.project.gamiai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.gamiai.domain.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    Submission findByJobId(String jobId);

    List<Submission> findByUserIdAndProblemIdOrderBySubmittedAtDesc(Integer userId, Integer problemId);

    List<Submission> findByUserIdOrderBySubmittedAtDesc(Integer userId);

    List<Submission> findByProblemIdOrderBySubmittedAtDesc(Integer problemId);
    
    boolean existsByUserIdAndProblemIdAndStatus(Integer userId, Integer problemId, String status);
}
package com.project.gamiai.repository;

import com.project.gamiai.domain.AiAssistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAssistanceRepository extends JpaRepository<AiAssistance, Integer> {
    Page<AiAssistance> findByUserIdAndLessonId(Integer userId, Integer lessonId, Pageable pageable);
    Page<AiAssistance> findByUserIdAndProblemId(Integer userId, Integer problemId, Pageable pageable);
}
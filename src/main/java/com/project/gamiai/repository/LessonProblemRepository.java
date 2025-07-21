package com.project.gamiai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.LessonProblem;

public interface LessonProblemRepository extends JpaRepository<LessonProblem, Integer> {
    LessonProblem findFirstByLessonIdAndIsRequiredTrue(Integer lessonId);
    LessonProblem findFirstByProblemId(Integer problemId); // Add this line
}

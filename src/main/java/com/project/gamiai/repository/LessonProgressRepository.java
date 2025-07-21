package com.project.gamiai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.project.gamiai.domain.LessonProgress;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {
    Optional<LessonProgress> findByUserIdAndLessonId(Integer userId, Integer lessonId);

    @Modifying
    @Transactional
    @Query("UPDATE LessonProgress lp SET lp.completed = false, lp.quizCompleted = false WHERE lp.userId = :userId AND lp.lessonId = :lessonId")
    void resetQuizProgress(@Param("userId") Integer userId, @Param("lessonId") Integer lessonId);
}
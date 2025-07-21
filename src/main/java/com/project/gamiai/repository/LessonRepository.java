package com.project.gamiai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.gamiai.domain.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    @Query(value = """
        SELECT 
            m.id AS module_id,
            m.title AS module_title,
            m.module_order AS module_order,
            l.id AS lesson_id,
            l.title AS lesson_title,
            l.is_sub_lesson AS is_sub_lesson,
            l.parent_lesson_id AS parent_lesson_id,
            l.lesson_order AS lesson_order,
            COALESCE(lp.completed, FALSE) AS completed
        FROM modules m
        JOIN lessons l ON m.id = l.module_id
        LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id AND lp.user_id = :userId
        WHERE m.course_id = :courseId
          AND (m.is_active IS NULL OR m.is_active = TRUE)
          AND (l.is_active IS NULL OR l.is_active = TRUE)
        ORDER BY m.id, m.module_order, l.lesson_order
    """, nativeQuery = true)
    List<Object[]> findModulesAndLessonsByCourseIdNative(@Param("courseId") Integer courseId, 
                                                         @Param("userId") Integer userId);

}
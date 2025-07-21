package com.project.gamiai.repository;

import java.util.Map;

import com.project.gamiai.dto.response.LessonDetailDto;
import com.project.gamiai.dto.response.LessonQuizSectionDto;

public interface LessonCustomRepository {
    LessonDetailDto findLessonDetailWithExamplesAndQuizzes(Integer lessonId);

    Map<Integer, String> getCorrectQuizAnswers(Integer lessonId);

    boolean isOnlyQuizLesson(Integer lessonId);

    LessonQuizSectionDto findQuizzesByLessonId(Integer lessonId, Integer userId);
}
package com.project.gamiai.service;

import java.util.List;
import java.util.Map;

import com.project.gamiai.dto.response.LessonDetailDto;
import com.project.gamiai.dto.response.LessonQuizSectionDto;

public interface LessonService {
    LessonDetailDto getLessonDetailWithExamplesAndQuizzes(Integer lessonId);

    List<Integer> checkQuizAnswers(Integer lessonId, Map<Integer, String> answers);

    boolean isOnlyQuizLesson(Integer lessonId);

    void markLessonCompleted(Integer userId, Integer lessonId, Boolean isOnlyQuizLesson);

    LessonQuizSectionDto findQuizzesByLessonId(Integer lessonId, Integer userId);
    void resetQuizProgress(Integer userId, Integer lessonId);
}
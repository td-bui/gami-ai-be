package com.project.gamiai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamiai.dto.request.QuizCheckRequest;
import com.project.gamiai.dto.response.LessonDetailDto;
import com.project.gamiai.dto.response.LessonQuizSectionDto;
import com.project.gamiai.dto.response.QuizCheckResponse;
import com.project.gamiai.service.LessonService;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping("/{id}/detail")
    public LessonDetailDto getLessonDetail(@PathVariable Integer id) {
        return lessonService.getLessonDetailWithExamplesAndQuizzes(id);
    }

    @PostMapping("/{id}/check-quiz")
    public QuizCheckResponse checkQuizAnswers(
            @PathVariable Integer id,
            @RequestBody QuizCheckRequest request,
            @RequestHeader("user-id") Integer userId // or get from auth context
    ) {
        // 1. Check answers
        List<Integer> wrongQuizIds = lessonService.checkQuizAnswers(id, request.getAnswers());

        // 2. If all correct, update lesson_progress
        boolean allCorrect = wrongQuizIds.isEmpty();
        QuizCheckResponse response = new QuizCheckResponse();
        if (allCorrect) {
            boolean isOnlyQuizLesson = lessonService.isOnlyQuizLesson(id);
            if (isOnlyQuizLesson){
                response.setCompleted(true);
            }
            lessonService.markLessonCompleted(userId, id, isOnlyQuizLesson);
        }
        response.setAllCorrect(allCorrect);
        response.setWrongQuizIds(wrongQuizIds);
        return response;
    }

    @GetMapping("/{lessonId}/quizzes")
    public LessonQuizSectionDto getLessonQuizzes(
            @PathVariable Integer lessonId,
            @RequestHeader("user-id") Integer userId
    ) {
        return lessonService.findQuizzesByLessonId(lessonId, userId);
    }

    @PutMapping("/{lessonId}/reset-quiz")
    public LessonQuizSectionDto resetQuiz(
            @PathVariable Integer lessonId,
            @RequestHeader("user-id") Integer userId
    ) {
        // Update lesson_progress: set completed and quiz_completed to false
        lessonService.resetQuizProgress(userId, lessonId);
        // Return the latest quiz section status
        return lessonService.findQuizzesByLessonId(lessonId, userId);
    }
}
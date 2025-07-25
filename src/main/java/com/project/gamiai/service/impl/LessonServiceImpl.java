package com.project.gamiai.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.gamiai.domain.Lesson;
import com.project.gamiai.domain.LessonProgress;
import com.project.gamiai.dto.response.LessonDetailDto;
import com.project.gamiai.dto.response.LessonQuizSectionDto;
import com.project.gamiai.repository.LessonCustomRepository;
import com.project.gamiai.repository.LessonProgressRepository;
import com.project.gamiai.repository.LessonRepository;
import com.project.gamiai.service.LessonService;
import com.project.gamiai.util.XpLevelUtil;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonCustomRepository lessonCustomRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private XpLevelUtil xpLevelUtil;

    @Override
    public LessonDetailDto getLessonDetailWithExamplesAndQuizzes(Integer id) {
        LessonDetailDto dto = lessonCustomRepository.findLessonDetailWithExamplesAndQuizzes(id);
        if (dto == null)
            throw new RuntimeException("Lesson not found with id: " + id);
        return dto;
    }

    @Override
    public List<Integer> checkQuizAnswers(Integer lessonId, Map<Integer, String> answers) {
        // Get correct answers from DB
        Map<Integer, String> correctAnswers = lessonCustomRepository.getCorrectQuizAnswers(lessonId);
        List<Integer> wrongQuizIds = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : correctAnswers.entrySet()) {
            Integer quizId = entry.getKey();
            String correct = entry.getValue();
            String userAnswer = answers.get(quizId);
            if (userAnswer == null || !userAnswer.trim().equalsIgnoreCase(correct.trim())) {
                wrongQuizIds.add(quizId);
            }
        }
        return wrongQuizIds;
    }

    @Override
    public boolean isOnlyQuizLesson(Integer lessonId) {
        return lessonCustomRepository.isOnlyQuizLesson(lessonId);
    }

    @Override
    @Transactional
    public int markLessonCompleted(Integer userId, Integer lessonId, Boolean isOnlyQuiz) {
        LessonProgress progress = lessonProgressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    LessonProgress lp = new LessonProgress();
                    lp.setUserId(userId);
                    lp.setLessonId(lessonId);
                    return lp;
                });

        boolean wasAlreadyCompleted = progress.getCompleted();

        progress.setQuizCompleted(true);
        if (isOnlyQuiz != null && isOnlyQuiz) {
            progress.setCompleted(true);
        }
        lessonProgressRepository.save(progress);
        
        if (progress.getCompleted() && !wasAlreadyCompleted) {
            Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found for XP calculation"));
            return xpLevelUtil.awardXpForLesson(userId, lesson.getDifficulty());
        }

        return 0;
    }

    @Override
    public LessonQuizSectionDto findQuizzesByLessonId(Integer lessonId, Integer userId) {
        return lessonCustomRepository.findQuizzesByLessonId(lessonId, userId);
    }

    @Override
    @Transactional
    public void resetQuizProgress(Integer userId, Integer lessonId) {
        lessonProgressRepository.resetQuizProgress(userId, lessonId);
    }

}
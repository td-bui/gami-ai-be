package com.project.gamiai.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.gamiai.dto.response.LessonDetailDto;
import com.project.gamiai.dto.response.LessonExampleDto;
import com.project.gamiai.dto.response.LessonQuizDto;
import com.project.gamiai.dto.response.LessonQuizSectionDto;
import com.project.gamiai.repository.LessonCustomRepository;

@Repository
public class LessonCustomRepositoryImpl implements LessonCustomRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public LessonDetailDto findLessonDetailWithExamplesAndQuizzes(Integer lessonId) {
        String sql = """
            SELECT
                l.id as lesson_id, l.title as lesson_title, l.content as lesson_content, l.difficulty as lesson_difficulty, l.module_id as lesson_module_id,
                e.id as example_id, e.code_id as example_code_id, e.code as example_code, e.description as example_description, e.explaination as example_explaination, e."output" as example_output
            FROM lessons l
            LEFT JOIN lesson_examples e ON l.id = e.lesson_id
            WHERE l.id = ?
            """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, lessonId);

        if (rows.isEmpty()) return null;

        LessonDetailDto lessonDto = new LessonDetailDto();
        Map<Integer, LessonExampleDto> exampleMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            if (lessonDto.getId() == null) {
                lessonDto.setId((Integer) row.get("lesson_id"));
                lessonDto.setTitle((String) row.get("lesson_title"));
                lessonDto.setContent((String) row.get("lesson_content"));
                lessonDto.setDifficulty((String) row.get("lesson_difficulty"));
                lessonDto.setModuleId(row.get("lesson_module_id") != null ? ((Number) row.get("lesson_module_id")).intValue() : null);
            }
            Integer exampleId = (Integer) row.get("example_id");
            if (exampleId != null && !exampleMap.containsKey(exampleId)) {
                LessonExampleDto ex = new LessonExampleDto();
                ex.setId(exampleId);
                ex.setCode((String) row.get("example_code"));
                ex.setDescription((String) row.get("example_description"));
                ex.setExplaination((String) row.get("example_explaination"));
                ex.setOutput((String) row.get("example_output"));
                ex.setCodeId((String) row.get("example_code_id"));
                exampleMap.put(exampleId, ex);
            }
        }

        lessonDto.setExamples(new ArrayList<>(exampleMap.values()));
        return lessonDto;
    }

    @Override
    public Map<Integer, String> getCorrectQuizAnswers(Integer lessonId) {
        String sql = "SELECT id, answer FROM lesson_quizzes WHERE lesson_id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, lessonId);
        Map<Integer, String> answers = new HashMap<>();
        for (Map<String, Object> row : rows) {
            answers.put((Integer) row.get("id"), (String) row.get("answer"));
        }
        return answers;
    }

    @Override
    public boolean isOnlyQuizLesson(Integer lessonId) {
        String sql = "SELECT only_quiz FROM lessons WHERE id = ?";
        Boolean onlyQuiz = jdbcTemplate.queryForObject(sql, Boolean.class, lessonId);
        return onlyQuiz != null && onlyQuiz;
    }

    @Override
    public LessonQuizSectionDto findQuizzesByLessonId(Integer lessonId, Integer userId) {
        String progressSql = """
            SELECT COALESCE(lp.completed, FALSE) AS completed, COALESCE(lp.quiz_completed, FALSE) AS quiz_completed
            FROM lessons l
            LEFT JOIN lesson_progress lp ON lp.lesson_id = l.id AND lp.user_id = ?
            WHERE l.id = ?
            """;
        Map<String, Object> progressRow = jdbcTemplate.queryForMap(progressSql, userId, lessonId);

        boolean completed = progressRow.get("completed") != null && (Boolean) progressRow.get("completed");
        boolean quizCompleted = progressRow.get("quiz_completed") != null && (Boolean) progressRow.get("quiz_completed");

        List<LessonQuizDto> quizzes = null;
        if (!completed && !quizCompleted) {
            String quizSql = """
                SELECT
                    q.id as quiz_id, q.question as quiz_question, o."options" as quiz_option
                FROM lesson_quizzes q
                LEFT JOIN lesson_quiz_options o ON q.id = o.lesson_quiz_id
                WHERE q.lesson_id = ?
                """;
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(quizSql, lessonId);

            Map<Integer, LessonQuizDto> quizMap = new LinkedHashMap<>();
            for (Map<String, Object> row : rows) {
                Integer quizId = (Integer) row.get("quiz_id");
                if (quizId != null) {
                    LessonQuizDto quiz = quizMap.get(quizId);
                    if (quiz == null) {
                        quiz = new LessonQuizDto();
                        quiz.setId(quizId);
                        quiz.setQuestion((String) row.get("quiz_question"));
                        quiz.setOptions(new ArrayList<>());
                        quizMap.put(quizId, quiz);
                    }
                    String option = (String) row.get("quiz_option");
                    if (option != null && !quiz.getOptions().contains(option)) {
                        quiz.getOptions().add(option);
                    }
                }
            }
            quizzes = new ArrayList<>(quizMap.values());
        }

        LessonQuizSectionDto dto = new LessonQuizSectionDto();
        dto.setCompleted(completed);
        dto.setQuizCompleted(quizCompleted);
        dto.setQuizzes(quizzes);
        return dto;
    }
}
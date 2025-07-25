package com.project.gamiai.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.gamiai.domain.Problem;
import com.project.gamiai.dto.response.ProblemSearchDto;

public interface ProblemRepository extends JpaRepository<Problem, Integer>, JpaSpecificationExecutor<Problem> {

    @Query(
        value = """
            SELECT DISTINCT ON (p.id)
                p.id AS id,
                p.title AS title,
                p.difficulty AS difficulty,
                t.name AS topicName,
                CASE WHEN EXISTS (
                    SELECT 1 FROM submissions s
                    WHERE s.problem_id = p.id AND s.user_id = :userId AND s.status = 'Accepted'
                ) THEN true ELSE false END AS isSolved
            FROM problems p
            LEFT JOIN problem_tags pt ON pt.problem_id = p.id
            LEFT JOIN topics t ON t.code = pt.tags
            WHERE (:name IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:difficulty IS NULL OR :difficulty = 'all' OR p.difficulty = :difficulty)
              AND (:topic IS NULL OR :topic = 'all' OR t.code = :topic)
              AND (
                :solved IS NULL OR :solved = 'all'
                OR (:solved = 'solved' AND EXISTS (
                    SELECT 1 FROM submissions s
                    WHERE s.problem_id = p.id AND s.user_id = :userId AND s.status = 'Accepted'
                ))
                OR (:solved = 'unsolved' AND NOT EXISTS (
                    SELECT 1 FROM submissions s
                    WHERE s.problem_id = p.id AND s.user_id = :userId AND s.status = 'Accepted'
                ))
              )
            ORDER BY p.id, t.name
            """,
        countQuery = """
            SELECT COUNT(DISTINCT p.id)
            FROM problems p
            LEFT JOIN problem_tags pt ON pt.problem_id = p.id
            LEFT JOIN topics t ON t.code = pt.tags
            WHERE (:name IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:difficulty IS NULL OR :difficulty = 'all' OR p.difficulty = :difficulty)
              AND (:topic IS NULL OR :topic = 'all' OR t.code = :topic)
              AND (
                :solved IS NULL OR :solved = 'all'
                OR (:solved = 'solved' AND EXISTS (
                    SELECT 1 FROM submissions s
                    WHERE s.problem_id = p.id AND s.user_id = :userId AND s.status = 'Accepted'
                ))
                OR (:solved = 'unsolved' AND NOT EXISTS (
                    SELECT 1 FROM submissions s
                    WHERE s.problem_id = p.id AND s.user_id = :userId AND s.status = 'Accepted'
                ))
              )
            """,
        nativeQuery = true
    )
    Page<ProblemSearchDto> searchProblemsNative(
        String name,
        String difficulty,
        String topic,
        String solved,
        Integer userId,
        Pageable pageable
    );

    @Query("SELECT p.difficulty, COUNT(p) FROM Problem p GROUP BY p.difficulty")
    List<Object[]> countTotalByDifficulty();

    default Map<String, Long> findTotalCountByDifficulty() {
        return countTotalByDifficulty().stream()
            .collect(Collectors.toMap(obj -> (String) obj[0], obj -> (Long) obj[1]));
    }

    @Query("SELECT p.difficulty, COUNT(p) FROM Problem p JOIN UserProgress up ON p.id = up.problemId WHERE up.userId = :userId AND up.solved = true GROUP BY p.difficulty")
    List<Object[]> countSolvedByDifficulty(@Param("userId") Integer userId);

    default Map<String, Long> findSolvedCountByDifficulty(Integer userId) {
        return countSolvedByDifficulty(userId).stream()
            .collect(Collectors.toMap(obj -> (String) obj[0], obj -> (Long) obj[1]));
    }
}
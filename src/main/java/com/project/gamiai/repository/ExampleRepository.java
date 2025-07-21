package com.project.gamiai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.gamiai.domain.LessonExample;

public interface ExampleRepository extends CrudRepository<LessonExample, Integer> {
    @Query("SELECT e.executableCode FROM LessonExample e WHERE e.id = :id")
    Optional<String> findExecutableCodeById(Integer id);
}
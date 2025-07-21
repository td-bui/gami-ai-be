package com.project.gamiai.service;

import java.util.List;

import com.project.gamiai.dto.response.ModuleLessonDto;

public interface CourseService {
    List<ModuleLessonDto> getModulesAndLessons(Integer courseId, Integer userId);
}
package com.project.gamiai.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.gamiai.dto.response.ModuleLessonDto;
import com.project.gamiai.repository.LessonRepository;
import com.project.gamiai.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public List<ModuleLessonDto> getModulesAndLessons(Integer courseId, Integer userId) {
        List<Object[]> rows = lessonRepository.findModulesAndLessonsByCourseIdNative(courseId, userId);
        return rows.stream().map(row -> {
            ModuleLessonDto dto = new ModuleLessonDto();
            dto.setModuleId((Integer) row[0]);
            dto.setModuleTitle((String) row[1]);
            dto.setModuleOrder(row[2] != null ? ((Number) row[2]).intValue() : null);
            dto.setLessonId((Integer) row[3]);
            dto.setLessonTitle((String) row[4]);
            dto.setIsSubLesson(row[5] != null ? (Boolean) row[5] : false);
            dto.setParentLessonId(row[6] != null ? ((Number) row[6]).intValue() : null);
            dto.setLessonOrder(row[7] != null ? ((Number) row[7]).intValue() : null);
            dto.setCompleted(row[8] != null && (Boolean) row[8]);
            return dto;
        }).toList();
    }
}
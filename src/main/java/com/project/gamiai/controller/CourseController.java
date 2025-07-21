package com.project.gamiai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamiai.dto.response.ModuleLessonDto;
import com.project.gamiai.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/{courseId}/modules-lessons")
    public List<ModuleLessonDto> getModulesAndLessons(
            @PathVariable Integer courseId,
            @RequestHeader("user-id") Integer userId
    ) {
        return courseService.getModulesAndLessons(courseId, userId);
    }
}
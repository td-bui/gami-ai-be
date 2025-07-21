package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class ModuleLessonDto {
    private Integer moduleId;
    private String moduleTitle;
    private Integer moduleOrder;
    private Integer lessonId;
    private String lessonTitle;
    private Boolean isSubLesson;
    private Integer parentLessonId;
    private Integer lessonOrder;
    private Boolean completed;
}
package com.project.gamiai.controller;

import com.project.gamiai.domain.AiAssistance;
import com.project.gamiai.service.AIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @GetMapping("/conversations")
    public Page<AiAssistance> getConversations(
            @RequestHeader("user-id") Integer userId,
            @RequestParam(required = false) Integer lessonId,
            @RequestParam(required = false) Integer problemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return aiService.getConversations(userId, lessonId, problemId, page, size);
    }
}
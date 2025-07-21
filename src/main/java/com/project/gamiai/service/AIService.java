package com.project.gamiai.service;

import org.springframework.data.domain.Page;
import com.project.gamiai.domain.AiAssistance;


public interface  AIService {
    Page<AiAssistance> getConversations(Integer userId, Integer lessonId, Integer problemId, int page, int size);
}
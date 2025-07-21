package com.project.gamiai.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.gamiai.domain.AiAssistance;
import com.project.gamiai.repository.AiAssistanceRepository;
import com.project.gamiai.service.AIService;


@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private AiAssistanceRepository aiAssistanceRepository;

    @Override
    public Page<AiAssistance> getConversations(Integer userId, Integer lessonId, Integer problemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateTime"));
        Page<AiAssistance> resultPage;
        if (lessonId != null) {
            resultPage = aiAssistanceRepository.findByUserIdAndLessonId(userId, lessonId, pageable);
        } else if (problemId != null) {
            resultPage = aiAssistanceRepository.findByUserIdAndProblemId(userId, problemId, pageable);
        } else {
            return Page.empty(pageable);
        }
        // Reverse the content list
        List<AiAssistance> reversed = new ArrayList<>(resultPage.getContent());
        Collections.reverse(reversed);
        return new PageImpl<>(reversed, pageable, resultPage.getTotalElements());
    }
}

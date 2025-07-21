package com.project.gamiai.controller;

import com.project.gamiai.dto.response.TopicDto;
import com.project.gamiai.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    // GET /api/topics/public
    @GetMapping("/public")
    public List<TopicDto> getPublicTopics() {
        return topicService.findAllPublicTopics();
    }
}
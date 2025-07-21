package com.project.gamiai.service;

import java.util.List;

import com.project.gamiai.dto.response.TopicDto;

public interface TopicService {
    List<TopicDto> findAllPublicTopics();
}
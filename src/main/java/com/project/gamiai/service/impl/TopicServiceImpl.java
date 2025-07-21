package com.project.gamiai.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.gamiai.domain.Topic;
import com.project.gamiai.dto.response.TopicDto;
import com.project.gamiai.repository.TopicRepository;
import com.project.gamiai.service.TopicService;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Override
    public List<TopicDto> findAllPublicTopics() {
        List<Topic> topics = topicRepository.findByIsPublicTrue();
        return topics.stream()
                .map(t -> new TopicDto(
                        t.getId(),
                        t.getName(),
                        t.getNumberOfProblems(),
                        t.getCode()
                ))
                .collect(Collectors.toList());
    }
}
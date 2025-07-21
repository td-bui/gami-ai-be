package com.project.gamiai.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findByIsPublicTrue();
    List<Topic> findByCodeIn(Set<String> codes);
}
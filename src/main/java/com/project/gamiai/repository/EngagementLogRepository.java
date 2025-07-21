package com.project.gamiai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.EngagementLog;

public interface EngagementLogRepository extends JpaRepository<EngagementLog, Integer>{
    
}

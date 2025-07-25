package com.project.gamiai.repository;

import java.util.List;
import java.util.Optional; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gamiai.domain.Leaderboard;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {
    Optional<Leaderboard> findByUserId(Integer userId);
    List<Leaderboard> findTop5ByOrderByXpDesc(); // For leaderboard
}

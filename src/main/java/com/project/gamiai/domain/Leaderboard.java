package com.project.gamiai.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaderboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {
    @Id
    private Integer userId;
    private String level;
    private Integer xp;
    private Integer problemsSolved = 0;
    private Integer lessonsCompleted = 0;
}
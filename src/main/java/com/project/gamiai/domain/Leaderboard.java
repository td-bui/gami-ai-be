package com.project.gamiai.domain;

import jakarta.persistence.*;
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
    private Integer user_id;
    private Integer rank;
    private Integer xp;
    private Integer problemsSolved;
}
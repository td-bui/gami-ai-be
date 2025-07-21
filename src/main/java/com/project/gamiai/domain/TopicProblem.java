package com.project.gamiai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "topic_problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicProblem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer topic_id;
    private Integer problem_id;
}
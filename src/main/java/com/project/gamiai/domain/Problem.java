package com.project.gamiai.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    private String difficulty;
    @ElementCollection
    private java.util.List<String> tags;
    @Column(columnDefinition = "text")
    private String constraints;
    @Column(columnDefinition = "text")
    private String examples;
    @Column(columnDefinition = "text")
    private String starterCode;
    @Column(columnDefinition = "text")
    private String solutionCode;
    private Integer createdById;
    private LocalDateTime createdAt;
    private Integer numberOfAttempts;
    private Integer numberOfAccepted;
    private Boolean isHtml = true;
}
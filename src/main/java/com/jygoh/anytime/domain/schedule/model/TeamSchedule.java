package com.jygoh.anytime.domain.schedule.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long teamId;

    private int priority;

    @Builder
    public TeamSchedule(String title, String description, LocalDateTime startTime, LocalDateTime endTime, Long teamId, int priority) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teamId = teamId;
        this.priority = priority;
    }

    public void updateSchedule(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateTime(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void updatePriority(int priority) {
        this.priority = priority;
    }
}

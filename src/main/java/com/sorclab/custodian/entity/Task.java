package com.sorclab.custodian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80)
    private String description;

    private LocalDateTime updatedAt;

    private LocalDateTime expirationDate;
    private int timerDurationDays; // days until task expires

    // allows for Task to be created, not expired timer, but not completed task
    private boolean isComplete;
}

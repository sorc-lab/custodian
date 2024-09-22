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

    @Column(unique = true, length = 25)
    private String label;

    @Column(length = 80)
    private String description;

    // TODO: Remove after it is phased out in favor of 'updatedAt'.
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime expirationDate;
    private int timerDurationDays; // days until task expires

    // TODO: Consider removing DB stored state. We don't need it consider we have data points needed.
    // NOTE: expirationDate is all we need for display util to calculate for us.
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    // allows for Task to be created, not expired timer, but not completed task
    private boolean isComplete;
}

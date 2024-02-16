package com.sorclab.custodianserver.entity;

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

    private String label;

    // TODO: Stores as comma-delimited String. Can I avoid VARCHAR(32000) schema?
    private String description;

    private LocalDateTime createdAt;

    // days until task expires
    private int timerDurationDays;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime expirationDate;
}

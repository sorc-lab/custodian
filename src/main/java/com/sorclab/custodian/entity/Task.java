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

    private LocalDateTime createdAt;
    private LocalDateTime expirationDate;
    private int timerDurationDays; // days until task expires

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}

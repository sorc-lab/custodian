package com.sorclab.custodianserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private int timerDuration;

    // TODO: Change to enum
    private String status;
}

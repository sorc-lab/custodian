package com.sorclab.custodian.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String label;
    private String description;
    private LocalDateTime createdAt;
    private int timerDurationDays;
    private LocalDateTime expirationDate;
    private String status;
}

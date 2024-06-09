package com.sorclab.custodianserver.model;

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
    private String label;
    private String description;
    private LocalDateTime createdAt;
    private int timerDurationDays;
    private String status;
}

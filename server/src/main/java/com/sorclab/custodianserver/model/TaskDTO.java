package com.sorclab.custodianserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// TODO: Check for usages once done and slim it down.
//  NOTE: Need to evaluate the client side DTO first to see what it needs from this DTO.

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

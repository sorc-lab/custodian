package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ShellDisplay {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    // TODO: Use orange if Task is complete but near certain fraction until expiration.
    private static final String ANSI_ORANGE = ANSI_RED + ANSI_YELLOW;

    public void displayTasks(List<Task> tasks) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%-5s%-25s%-80s%-15s%n", "ID", "LABEL", "DESCRIPTION", "TIMER"));
        stringBuilder.append(String.format("%-5s%-25s%-80s%-15s%n", "--", "-----", "-----------", "-----"));

        tasks.forEach(task -> {
            String color;

            boolean isExpired = LocalDateTime.now().isAfter(task.getExpirationDate());
            if (isExpired || !task.isComplete()) {
                color = ANSI_RED;
            } else {
                color = ANSI_GREEN;
            }

            stringBuilder.append(String.format("%s%-5d%-25s%-80s%-15s%s%n",
                    color,
                    task.getId(),
                    task.getLabel(),
                    task.getDescription(),
                    "every " + task.getTimerDurationDays() + " days",
                    ANSI_RESET));
        });

        System.out.println(stringBuilder);
    }

    public void displayTask(Task task) {
        System.out.println("ID               : " + task.getId());
        System.out.println("Label            : " + task.getLabel());
        System.out.println("Description      : " + task.getDescription());
        System.out.println("updatedAt        : " + task.getUpdatedAt());
        System.out.println("timerDurationDays: " + task.getTimerDurationDays());
        System.out.println("expirationDate   : " + task.getExpirationDate());
    }
}

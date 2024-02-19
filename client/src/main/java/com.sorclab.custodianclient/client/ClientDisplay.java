package com.sorclab.custodianclient.client;

import com.sorclab.custodianclient.model.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClientDisplay {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_ORANGE = ANSI_RED + ANSI_YELLOW;

    private static final String STATUS_NEW = "NEW";
    private static final String STATUS_EXPIRED = "EXPIRED";
    private static final String STATUS_COMPLETE = "COMPLETE";

    public void displayTasks(List<TaskDTO> tasks) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%-5s%-25s%-80s%-15s%n", "ID", "LABEL", "DESCRIPTION", "TIMER"));
        stringBuilder.append(String.format("%-5s%-25s%-80s%-15s%n", "--", "-----", "-----------", "-----"));

        tasks.forEach(task -> {

            String status = task.getStatus();

            String color = null;
            if (STATUS_NEW.equals(status) || STATUS_EXPIRED.equals(status)) {
                color = ANSI_RED;
            } else if (STATUS_COMPLETE.equals(status)) {
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

    public void displayTask(TaskDTO task) {
        System.out.println("ID               : " + task.getId());
        System.out.println("Label            : " + task.getLabel());
        System.out.println("Description      : " + task.getDescription());
        System.out.println("createdAt        : " + task.getCreatedAt());
        System.out.println("timerDurationDays: " + task.getTimerDurationDays());
        System.out.println("expirationDate   : " + task.getExpirationDate());
        System.out.println("status           : " + task.getStatus());
    }
}

package com.sorclab.custodianclient.client;

import com.sorclab.custodianclient.model.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        stringBuilder.append(String.format("%-5s%-25s%-50s%n", "ID", "LABEL", "DESCRIPTION"));
        stringBuilder.append(String.format("%-5s%-25s%-50s%n", "--", "-----", "-----------"));

        tasks.forEach(task -> {

            String status = task.getStatus();

            String color = null;
            if (STATUS_NEW.equals(status) || STATUS_EXPIRED.equals(status)) {
                color = ANSI_RED;
            } else if (STATUS_COMPLETE.equals(status)) {
                color = ANSI_GREEN;
            }

            stringBuilder.append(String.format("%s%-5d%-25s%-50s%s%n",
                    color,
                    task.getId(),
                    task.getLabel(),
                    task.getDescription(),
                    ANSI_RESET));
        });

        System.out.println(stringBuilder);
    }
}

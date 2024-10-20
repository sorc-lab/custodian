package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShellDisplay {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_ORANGE = ANSI_RED + ANSI_YELLOW;

    private static final String DISPLAY_TASK_LIST_HEADERS = String.format("%-5s%-80s%-15s%n", "ID", "DESCRIPTION", "TIMER");
    private static final String DISPLAY_TASK_LIST_HEADER_DIVIDER = String.format("%-5s%-80s%-15s%n", "--", "-----------", "-----");
    private static final String DISPLAY_TASK_LIST_FORMAT = "%s%-5d%-80s%-15s%s%n";

    private final ScreenPrinter screenPrinter;

    public void displayTasks(List<Task> tasks) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DISPLAY_TASK_LIST_HEADERS);
        stringBuilder.append(DISPLAY_TASK_LIST_HEADER_DIVIDER);

        tasks.forEach(task -> {
            String ansiColor;

            boolean isExpired = LocalDateTime.now().isAfter(task.getExpirationDate());
            if (isExpired || !task.isComplete()) {
                ansiColor = ANSI_RED;
            } else if (LocalDateTime.now().isAfter(task.getExpirationDate().minusDays(2))) {
                ansiColor = ANSI_ORANGE; // warning expiration is coming in 2 days
            } else {
                ansiColor = ANSI_GREEN;
            }

            DisplayTask displayTask = DisplayTask.builder()
                    .format(DISPLAY_TASK_LIST_FORMAT)
                    .ansiColor(ansiColor)
                    .taskId(task.getId())
                    .description(task.getDescription())
                    .durationDescription("every " + task.getTimerDurationDays() + " days")
                    .build();

            stringBuilder.append(screenPrinter.formatTaskWithColor(displayTask));
        });

        screenPrinter.printLine(String.valueOf(stringBuilder));
    }

    public void displayTask(Task task) {
        screenPrinter.printLine("ID               : " + task.getId());
        screenPrinter.printLine("Description      : " + task.getDescription());
        screenPrinter.printLine("updatedAt        : " + task.getUpdatedAt());
        screenPrinter.printLine("timerDurationDays: " + task.getTimerDurationDays());
        screenPrinter.printLine("expirationDate   : " + task.getExpirationDate());
    }
}

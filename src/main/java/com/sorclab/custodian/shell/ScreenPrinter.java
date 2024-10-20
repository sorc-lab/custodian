package com.sorclab.custodian.shell;

import org.springframework.stereotype.Component;

@Component
class ScreenPrinter {
    private static final String ANSI_RESET = "\u001B[0m";

    void printLine(String line) {
        System.out.println(line);
    }

    String formatTaskWithColor(DisplayTask displayTask) {
        return String.format(
                displayTask.getFormat(),
                displayTask.getAnsiColor(),
                displayTask.getTaskId(),
                displayTask.getDescription(),
                displayTask.getDurationDescription(),
                ANSI_RESET
        );
    }
}

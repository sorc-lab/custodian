package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShellDisplayTest {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_ORANGE = ANSI_RED + ANSI_YELLOW;

    private static final String DISPLAY_TASK_LIST_FORMAT = "%s%-5d%-80s%-15s%s%n";

    @Mock
    ScreenPrinter screenPrinter;

    @InjectMocks
    ShellDisplay shellDisplay;

    @Captor ArgumentCaptor<DisplayTask> displayTaskArgCaptor;
    @Captor ArgumentCaptor<String> strArgCaptor;

    @Test
    void displayTasks() {
        Task task1 = Task.builder()
                .id(1L)
                .updatedAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(7))
                .timerDurationDays(7)
                .isComplete(true)
                .description("test-description-1")
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .updatedAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(30))
                .timerDurationDays(30)
                .isComplete(false)
                .description("test-description-2")
                .build();

        shellDisplay.displayTasks(List.of(task1, task2));

        verify(screenPrinter).printLine(strArgCaptor.capture());

        String actualOutput = strArgCaptor.getValue();

        // due to mocking, the actual tasks are null, but we can check there are two nulls.
        String expectedOutput = "ID   DESCRIPTION                                                                     TIMER          \n" +
                "--   -----------                                                                     -----          \n" +
                "nullnull";

        // only difference in output should be in CRLF vs. LF line endings
        String normalizedActual = actualOutput.replace("\r\n", "\n").replace("\r", "\n");
        String normalizedExpected = expectedOutput.replace("\r\n", "\n").replace("\r", "\n");

        assertThat(normalizedActual).isEqualTo(normalizedExpected);
    }

    @Test
    void displayTasks_IsComplete_AnsiGreen() {
        Task task = Task.builder()
                .id(1L)
                .updatedAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(7))
                .timerDurationDays(7)
                .isComplete(true)
                .description("test-description")
                .build();

        shellDisplay.displayTasks(List.of(task));

        verify(screenPrinter).formatTaskWithColor(displayTaskArgCaptor.capture());

        DisplayTask displayTask = displayTaskArgCaptor.getValue();
        assertThat(displayTask.getFormat()).isEqualTo(DISPLAY_TASK_LIST_FORMAT);
        assertThat(displayTask.getAnsiColor()).isEqualTo(ANSI_GREEN);
        assertThat(displayTask.getTaskId()).isEqualTo(1L);
        assertThat(displayTask.getDescription()).isEqualTo("test-description");
        assertThat(displayTask.getDurationDescription()).isEqualTo("every 7 days");
    }

    @Test
    void displayTasks_IsAlmostExpired_AnsiOrange() {
        Task task = Task.builder()
                .id(1L)
                .expirationDate(LocalDateTime.now().plusDays(1))
                .isComplete(true)
                .build();

        shellDisplay.displayTasks(List.of(task));

        verify(screenPrinter).formatTaskWithColor(displayTaskArgCaptor.capture());

        DisplayTask displayTask = displayTaskArgCaptor.getValue();
        assertThat(displayTask.getAnsiColor()).isEqualTo(ANSI_ORANGE);
    }

    @Test
    void displayTasks_IsExpired_AnsiRed() {
        Task task = Task.builder()
                .id(1L)
                .expirationDate(LocalDateTime.now().minusDays(1))
                .isComplete(true)
                .build();

        shellDisplay.displayTasks(List.of(task));

        verify(screenPrinter).formatTaskWithColor(displayTaskArgCaptor.capture());

        DisplayTask displayTask = displayTaskArgCaptor.getValue();
        assertThat(displayTask.getAnsiColor()).isEqualTo(ANSI_RED);
    }

    @Test
    void displayTasks_IsNotComplete_AnsiRed() {
        Task task = Task.builder()
                .id(1L)
                .expirationDate(LocalDateTime.now().plusDays(1))
                .isComplete(false)
                .build();

        shellDisplay.displayTasks(List.of(task));

        verify(screenPrinter).formatTaskWithColor(displayTaskArgCaptor.capture());

        DisplayTask displayTask = displayTaskArgCaptor.getValue();
        assertThat(displayTask.getAnsiColor()).isEqualTo(ANSI_RED);
    }

    @Test
    void displayTask() {
        Task task = Task.builder()
                .id(1L)
                .updatedAt(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusDays(7))
                .timerDurationDays(7)
                .isComplete(true)
                .description("test-description-1")
                .build();

        shellDisplay.displayTask(task);

        verify(screenPrinter).printLine("ID               : 1");
        verify(screenPrinter).printLine("Description      : test-description-1");
        verify(screenPrinter).printLine(contains("updatedAt        : "));
        verify(screenPrinter).printLine("timerDurationDays: 7");
        verify(screenPrinter).printLine(contains("expirationDate   : "));
    }
}

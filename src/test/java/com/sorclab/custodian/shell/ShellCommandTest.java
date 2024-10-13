package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/* TODO: Mock Slf4J to assert log.error, etc.
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    private TaskService taskService;

    private Logger mockLogger;

    @BeforeEach
    public void setUp() throws Exception {
        taskService = new TaskService();
        mockLogger = Mockito.mock(Logger.class);
        setMockLogger(taskService, mockLogger);
    }

    private void setMockLogger(Object target, Logger mockLogger) throws Exception {
        Field loggerField = target.getClass().getDeclaredField("log");
        loggerField.setAccessible(true);
        loggerField.set(target, mockLogger);
    }

    @Test
    public void testLogging() {
        taskService.processTask();

        // Verify that specific log messages were logged
        verify(mockLogger).info("Processing task...");
        verify(mockLogger).error("An error occurred while processing the task");
    }
}
 */

@ExtendWith(MockitoExtension.class)
public class ShellCommandTest {
    @Mock private TaskService taskService;
    @Mock private ShellDisplay shellDisplay;

    @InjectMocks
    private ShellCommand shellCommand;

    @Test
    public void add() {
        Task expectedTask = Task.builder()
                .label("test-label")
                .description("test-description")
                .timerDurationDays(1)
                .build();

        shellCommand.add("test-label", "test-description", 1);

        verify(taskService).createTask(eq(expectedTask));
    }

    @Test
    public void list() {
        when(taskService.getTasks()).thenReturn(List.of(Task.builder().build()));
        shellCommand.list();
        verify(shellDisplay).displayTasks(List.of(Task.builder().build()));
    }

    @Test
    public void view() {
        when(taskService.getTask(1L)).thenReturn(Task.builder().build());
        shellCommand.view(1L, null);
        verify(shellDisplay).displayTask(Task.builder().build());
    }

    @Test
    public void view_NullId_LogsError() {

    }
}

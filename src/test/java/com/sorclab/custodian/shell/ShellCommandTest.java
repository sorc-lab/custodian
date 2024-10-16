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

@ExtendWith(MockitoExtension.class)
public class ShellCommandTest {
    @Mock private TaskService taskService;
    @Mock private ShellDisplay shellDisplay;

    @InjectMocks
    private ShellCommand shellCommand;

    @Test
    public void add() {
        Task expectedTask = Task.builder()
                .description("test-description")
                .timerDurationDays(1)
                .build();

        shellCommand.add("test-description", 1);

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
        shellCommand.view(1L);
        verify(shellDisplay).displayTask(Task.builder().build());
    }

    @Test
    public void delete() {
        shellCommand.delete(1L);
        verify(taskService).deleteTaskById(eq(1L));
    }

    @Test
    public void complete() {
        shellCommand.complete(1L);
        verify(taskService).completeTaskById(1L);
    }
}

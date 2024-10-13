package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

// TODO: Clean up help docs and examples. Consider removing -- tags, not required but helpful?

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class ShellCommand {
    private final ShellDisplay shellDisplay;
    private final TaskService taskService;

    @ShellMethod(value = "Add a Task")
    public void add(
            @ShellOption(value = "--description", help = "e.g. --description 'Mop floors and clean surfaces'") String description,
            @ShellOption(value = "--timer-duration", help = "Set number of days e.g. --timer-duration 7") int timerDuration)
    {
        Task task = Task.builder()
                .description(description)
                .timerDurationDays(timerDuration)
                .build();

        taskService.createTask(task);
    }

    @ShellMethod(value = "List Tasks")
    public void list() {
        shellDisplay.displayTasks(taskService.getTasks());
    }

    @ShellMethod(value = "View a Task")
    public void view(@ShellOption(value = "--id", help = "e.g. --id 10") Long id) {
        shellDisplay.displayTask(taskService.getTask(id));
    }

    @ShellMethod(value = "Delete Task by id or label")
    public void delete(@ShellOption(value = "--id", help = "e.g. --id 10") Long id) {
        taskService.deleteTaskById(id);
    }

    @ShellMethod(value = "Complete task by id or label")
    public void complete(@ShellOption(value = "--id", help = "e.g. --id 10") Long id) {
        taskService.completeTaskById(id);
    }
}

package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class ShellCommand {
    private final ShellDisplay shellDisplay;
    private final TaskService taskService;

    @ShellMethod(value = "Add a Task")
    public void add(
            @ShellOption(value = "--label", help = "e.g. --label 'Kitchen'") String label,
            @ShellOption(value = "--description", help = "e.g. --description 'Mop floors and clean surfaces'") String description,
            @ShellOption(value = "--timer-duration", help = "Set number of days e.g. --timer-duration 7") int timerDuration)
    {
        Task task = Task.builder()
                .label(label)
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
    public void view(
            @ShellOption(value = "--id", defaultValue = ShellOption.NULL,help = "e.g. --id 10") Long id,
            @ShellOption(value = "--label", defaultValue = ShellOption.NULL,help = "e.g. --label 'Kitchen'") String label)
    {
        if (id == null) {
            throw new RuntimeException("View MUST provide an id or label!");
        }

        // TODO: Add viewByLabel
        shellDisplay.displayTask(taskService.getTask(id));
    }

    @ShellMethod(value = "Delete Task by id or label")
    public void delete(
            @ShellOption(value = "--id", defaultValue = ShellOption.NULL, help = "e.g. --id 10") Long id,
            @ShellOption(value = "--label", defaultValue = ShellOption.NULL, help = "e.g. --label 'Kitchen'") String label)
    {
        if (id != null) {
            taskService.deleteTaskById(id);
            return;
        }

        if (StringUtils.hasText(label)) {
            taskService.deleteTaskByLabel(label);
            return;
        }

        log.error("Delete MUST provide an id or label!");
    }

    @ShellMethod(value = "Complete task by id or label")
    public void complete(
            @ShellOption(value = "--id", defaultValue = ShellOption.NULL, help = "e.g. --id 10") Long id,
            @ShellOption(value = "--label", defaultValue = ShellOption.NULL, help = "e.g. --label 'Kitchen'") String label)
    {
        if (id != null) {
            taskService.completeTaskById(id);
            return;
        }

        // TODO: Add complete by label

        log.error("Complete Task MUST provide an id or label!");
    }
}

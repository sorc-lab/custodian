package com.sorclab.custodianclient.shell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianclient.client.ClientService;
import com.sorclab.custodianclient.model.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class ShellCommand {
    private final ClientService clientService;

    @ShellMethod(value = "Add a Task")
    public void add(
            @ShellOption(value = "--label") String label,
            @ShellOption(value = "--description") String description,
            @ShellOption(value = "--timer-duration") int timerDuration)
    {
        TaskDTO taskDTO = TaskDTO.builder()
                .label(label)
                .description(description)
                .timerDuration(timerDuration)
                .build();

        clientService.addTask(taskDTO);
    }

    @ShellMethod(value = "List Tasks")
    public void list() {
        List<TaskDTO> tasks = clientService.getTasks();

        try {
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(tasks));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        tasks.forEach(task -> {
            System.out.println(task.getId());
            System.out.println(task.getLabel());
            System.out.println(task.getDescription());
            System.out.println(task.getTimerDuration());
        });
    }

    /*
        - get tasks -> prints all tasks and their color-coded status in brief, no descriptions
        - get task -> prints an individual tasks and all the details
        - edit task -> edit tasks label, desc, and timer, but not status, that is driven by cron
        - add task -> adds a task
        - remove task -> removes a task
        - complete task -> marks a task completed, which triggers the cron to reset based on timer
        - get alerts/notifications (needs to also print these on startup somehow)
        -
     */

    /* Alert system
    The alert system needs to be cron schedules that write to DB the task status
    GetAlerts will ONLY return expired items or items soon to expire in orange etc.
    GetTasks will list them out and apply a color to ALL tasks completed and expired/soon to expire
     */
}

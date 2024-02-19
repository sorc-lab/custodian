package com.sorclab.custodianclient.shell;

import com.sorclab.custodianclient.client.ClientDisplay;
import com.sorclab.custodianclient.client.ClientService;
import com.sorclab.custodianclient.model.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class ShellCommand {
    private final ClientService clientService;
    private final ClientDisplay clientDisplay;

    @ShellMethod(value = "Add a Task")
    public void add(
            @ShellOption(value = "--label", help = "e.g. --label 'Kitchen'") String label,
            @ShellOption(value = "--description", help = "e.g. --description 'Mop floors and clean surfaces'") String description,
            @ShellOption(value = "--timer-duration", help = "Set number of days e.g. --timer-duration 7") int timerDuration)
    {
        TaskDTO taskDTO = TaskDTO.builder()
                .label(label)
                .description(description)
                .timerDurationDays(timerDuration)
                .build();

        clientService.addTask(taskDTO);
    }

    @ShellMethod(value = "List Tasks")
    public void list() {
        clientDisplay.displayTasks(clientService.getTasks());
    }

    @ShellMethod(value = "View a Task")
    public void view(
            @ShellOption(value = "--id", defaultValue = ShellOption.NULL,help = "e.g. --id 10") Long id,
            @ShellOption(value = "--label", defaultValue = ShellOption.NULL,help = "e.g. --label 'Kitchen'") String label)
    {
        if (id != null) {
            clientDisplay.displayTask(clientService.getTaskById(id));
            return;
        }

        // TODO: Add viewByLabel

        log.error("View MUST provide an id or label!");

    }

    @ShellMethod(value = "Delete Task by id or label")
    public void delete(
            @ShellOption(value = "--id", defaultValue = ShellOption.NULL, help = "e.g. --id 10") Long id,
            @ShellOption(value = "--label", defaultValue = ShellOption.NULL, help = "e.g. --label 'Kitchen'") String label)
    {
        if (id != null) {
            clientService.deleteTaskById(id);
            return;
        }

        if (StringUtils.hasText(label)) {
            clientService.deleteTaskByLabel(label);
            return;
        }

        log.error("Delete MUST provide an id or label!");
    }

    /*
        - get task -> prints an individual tasks and all the details
        - edit task -> edit tasks label, desc, and timer, but not status, that is driven by cron
        - complete task -> marks a task completed, which triggers the cron to reset based on timer
        - get alerts/notifications (needs to also print these on startup somehow)
     */

    /* Alert system
    The alert system needs to be cron schedules that write to DB the task status
    GetAlerts will ONLY return expired items or items soon to expire in orange etc.
    GetTasks will list them out and apply a color to ALL tasks completed and expired/soon to expire
     */
}

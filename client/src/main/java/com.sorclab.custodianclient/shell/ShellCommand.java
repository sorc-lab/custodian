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
    private final ObjectMapper objectMapper;

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
        List<TaskDTO> tasks = clientService.getTasks();

        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tasks));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

/*
Next to each item write the date completed and the due date for the task to be repeated.
ONCE A WEEK
Upstairs offices surface tops
Upstairs carpets and stairs vacuumed
Upstairs bathrooms mopped
Master bathroom toilet and surfaces
Upstairs Guest bathroom toilet and surfaces
Downstairs bathroom toilet and surfaces
Downstairs carpets vacuumed
Downstairs kitchen, bathroom and front room entry mopped
Downstairs kitchen and dining room surface tops

EVERY OTHER WEEK
Upstairs window sills
Upstairs bathrooms mopped
Master bathroom tub, shower & Windex mirror
Downstairs bathroom Windex mirror
Downstairs window sills
Upstairs Guest bathroom tub, shower & Windex mirror

ONCE A MONTH
Master bed changed
Mae’s bed changed
Master bathroom mats changed
Guest bathroom mats changed
 */

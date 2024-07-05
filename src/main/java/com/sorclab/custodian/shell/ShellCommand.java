package com.sorclab.custodian.shell;

import com.sorclab.custodian.model.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

/** TODO: Test via this pattern
     @SpringBootTest(properties = {
     InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
     ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
     })
     public class MyShellControllerTest {

     @Autowired
     private MyShellController myShellController;

     @Test
     public void testHelloCommand() {
     String result = myShellController.hello();
     assertThat(result).isEqualTo("Hello, world!");
     }

     @Test
     public void testGreetCommand() {
     String result = myShellController.greet("Alice");
     assertThat(result).isEqualTo("Hello, Alice!");
     }
     }
 */

@ShellComponent
@Slf4j
@RequiredArgsConstructor
public class ShellCommand {
    private final ShellService shellService;
    private final ShellDisplay shellDisplay;

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

        shellService.addTask(taskDTO);
    }

    @ShellMethod(value = "List Tasks")
    public void list() {
        shellDisplay.displayTasks(shellService.getTasks());
    }

    @ShellMethod(value = "View a Task")
    public void view(
            @ShellOption(value = "--id", defaultValue = ShellOption.NULL,help = "e.g. --id 10") Long id,
            @ShellOption(value = "--label", defaultValue = ShellOption.NULL,help = "e.g. --label 'Kitchen'") String label)
    {
        if (id != null) {
            shellDisplay.displayTask(shellService.getTaskById(id));
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
            shellService.deleteTaskById(id);
            return;
        }

        if (StringUtils.hasText(label)) {
            shellService.deleteTaskByLabel(label);
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
            shellService.completeTaskById(id);
            return;
        }

        // TODO: Add complete by label

        log.error("Complete Task MUST provide and id or label!");
    }

    /*
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

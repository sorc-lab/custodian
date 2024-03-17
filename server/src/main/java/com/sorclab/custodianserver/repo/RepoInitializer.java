package com.sorclab.custodianserver.repo;

import com.sorclab.custodianserver.Util.TasksFileUtil;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepoInitializer implements CommandLineRunner {
    private final TaskService taskService;
    private final TasksFileUtil tasksFileUtil;

    @Override
    public void run(String... args) {
        // will not overwrite existing data
        tasksFileUtil.createTasksFile();

        // no need to check if json file exists, will always exist
        List<TaskDTO> tasks = tasksFileUtil.getTasksFromTasksFile();
        taskService.createTasks(tasks);
        taskService.saveTasksToFilesystem(); // this will update task states.
    }
}

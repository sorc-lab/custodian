package com.sorclab.custodianserver.endpoint;

import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/task")
    public void createTask(@RequestBody TaskDTO taskDTO) {
        taskService.createTask(taskDTO);
    }

    @GetMapping("/task")
    public List<Task> getTasks() {
        return taskService.getTasks();
    }

    @DeleteMapping("/task/{id}")
    public void deleteTaskById(@PathVariable long id) {
        taskService.deleteTaskById(id);
    }

    @DeleteMapping("/task")
    public void deleteTaskByLabel(@RequestParam(name = "label") String label) {
        taskService.deleteTaskByLabel(label);
    }
}

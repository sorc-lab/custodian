package com.sorclab.custodian.endpoint;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.model.TaskDTO;
import com.sorclab.custodian.service.TaskService;
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

    @GetMapping("/task/{id}")
    public Task getTask(@PathVariable long id) {
        return taskService.getTask(id);
    }

    @PutMapping("/task/{id}")
    public void completeTaskById(@PathVariable long id) {
        taskService.completeTaskById(id);
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

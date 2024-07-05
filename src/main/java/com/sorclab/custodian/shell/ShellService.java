package com.sorclab.custodian.shell;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.model.TaskDTO;
import com.sorclab.custodian.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShellService {
    private final TaskService taskService;

    public void addTask(TaskDTO taskDTO) {
        taskService.createTask(taskDTO);
    }

    public List<TaskDTO> getTasks() {
        return convertTasksToDTOs(taskService.getTasks());
    }

    public TaskDTO getTaskById(long id) {
        return convertTaskToTaskDTO(taskService.getTask(id));
    }

    public void completeTaskById(long id) {
        taskService.completeTaskById(id);
    }

    public void deleteTaskById(long id) {
        taskService.deleteTaskById(id);
    }

    public void deleteTaskByLabel(String label) {
        taskService.deleteTaskByLabel(label);
    }

    private List<TaskDTO> convertTasksToDTOs(List<Task> tasks) {
        List<TaskDTO> taskDTOs = new ArrayList<>();
        tasks.forEach(task -> {
            TaskDTO taskDTO = TaskDTO.builder()
                    .id(task.getId())
                    .label(task.getLabel())
                    .description(task.getDescription())
                    .createdAt(task.getCreatedAt())
                    .timerDurationDays(task.getTimerDurationDays())
                    .expirationDate(task.getExpirationDate())
                    .status(String.valueOf(task.getStatus()))
                    .build();

            taskDTOs.add(taskDTO);
        });

        return taskDTOs;
    }

    private TaskDTO convertTaskToTaskDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .label(task.getLabel())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .timerDurationDays(task.getTimerDurationDays())
                .expirationDate(task.getExpirationDate())
                .status(String.valueOf(task.getStatus()))
                .build();
    }
}

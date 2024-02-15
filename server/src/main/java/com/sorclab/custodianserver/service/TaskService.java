package com.sorclab.custodianserver.service;

import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.repo.TaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepo taskRepo;

    public void createTask(Task task) {
        Task newTask = Task.builder()
                .label(task.getLabel())
                .description(task.getDescription())
                .createdAt(LocalDateTime.now())
                .timerDuration(task.getTimerDuration())
                .build();

        // TODO: If task exists, need to update it? Or better, error out and say already exists.
        taskRepo.save(newTask);
    }

    public List<Task> getTasks() {
        return taskRepo.findAll();
    }
}

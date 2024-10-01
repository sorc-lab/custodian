package com.sorclab.custodian.service;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.repo.TaskRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private static final int SECONDS_IN_24_HOURS = 86400;

    private final TaskRepo taskRepo;

    @Transactional
    public void createTask(Task task) {
        int secondsUntilExpiration = task.getTimerDurationDays() * SECONDS_IN_24_HOURS;
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(secondsUntilExpiration);

        Task newTask = Task.builder()
                .label(task.getLabel())
                .description(task.getDescription())
                .updatedAt(LocalDateTime.now())
                .timerDurationDays(task.getTimerDurationDays())
                .expirationDate(expirationDate)
                .isComplete(false)
                .build();

        taskRepo.save(newTask);
    }

    @Transactional
    public void completeTaskById(long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task found for id: " + id));

        task.setComplete(true);
        task.setUpdatedAt(LocalDateTime.now());
        task.setExpirationDate(task.getUpdatedAt().plusDays(task.getTimerDurationDays()));

        taskRepo.save(task);
    }

    public void deleteTaskById(long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Task found for id: " + id));

        taskRepo.delete(task);
    }

    public void deleteTaskByLabel(String label) {
        Task task = taskRepo.findByLabel(label)
                .orElseThrow(() -> new EntityNotFoundException("No task found for label: " + label));

        taskRepo.delete(task);
    }

    public List<Task> getTasks() {
        return taskRepo.findAll();
    }

    public Task getTask(long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task found for id: " + id));
    }
}

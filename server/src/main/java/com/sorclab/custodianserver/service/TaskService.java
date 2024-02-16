package com.sorclab.custodianserver.service;

import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.entity.TaskStatus;
import com.sorclab.custodianserver.repo.TaskRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final int SECONDS_IN_24_HOURS = 86400;

    private final TaskRepo taskRepo;

    @Transactional
    public void createTask(Task task) {
        LocalDateTime currentTime = LocalDateTime.now();

        int secondsUntilExpiration = task.getTimerDurationDays() * SECONDS_IN_24_HOURS;
        LocalDateTime expirationDate = currentTime.plusSeconds(secondsUntilExpiration);

        // TODO: validate the number of days. max should be 30?
        // TODO: Add unique constraint on label. Do not update on existing, throw error.

        Task newTask = Task.builder()
                .label(task.getLabel())
                .description(task.getDescription())
                .createdAt(LocalDateTime.now())
                .timerDurationDays(task.getTimerDurationDays())
                .expirationDate(expirationDate)
                .status(TaskStatus.NEW)
                .build();

        taskRepo.save(newTask);
    }

    public void deleteTaskById(long id) {

    }

    public void deleteTaskByLabel(String label) {

    }

    public void completeTaskById(long id) {
        // marks task complete and automatically triggers a scan to update ALL tasks status
    }

    public void completeTaskByLabel(String label) {
        // marks task complete and automatically triggers a scan to update ALL tasks status
    }

    public List<Task> getTasks() {
        return taskRepo.findAll();
    }

    private void updateAllTaskStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Task> tasks = taskRepo.findAll();
        tasks.forEach(task -> {
            LocalDateTime expirationDate = task.getExpirationDate();

            if (currentTime.isEqual(expirationDate) || currentTime.isAfter(expirationDate)) {
                task.setStatus(TaskStatus.EXPIRED);
                taskRepo.save(task);
            }

            // TODO: Need a calculation for NEAR_EXPIRATION after above check.
        });
    }
}

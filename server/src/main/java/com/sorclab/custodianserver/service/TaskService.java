package com.sorclab.custodianserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianserver.Util.TasksFileUtil;
import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.entity.TaskStatus;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.repo.TaskRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** H2 in-memory Database and Filesystem JSON
 * - DB init routine called to pull in JSON data from filesystem on startup
 * - Read methods directly from in-memory DB
 * - Write methods write to Filesystem AND in-memory DB
 * - Scheduled routines update Task status & sync current in-memory DB data to Filesystem
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private static final int SECONDS_IN_24_HOURS = 86400;

    private final TaskRepo taskRepo;
    private final ObjectMapper objectMapper;
    private final TasksFileUtil tasksFileUtil;

    @Transactional
    public void createTask(TaskDTO taskDTO) {
        int secondsUntilExpiration = taskDTO.getTimerDurationDays() * SECONDS_IN_24_HOURS;

        // TODO: Revisit this logic, but good enough for this iteration to test live.
        LocalDateTime expirationDate = taskDTO.getCreatedAt() != null
                ? taskDTO.getCreatedAt().plusSeconds(secondsUntilExpiration)
                : LocalDateTime.now().plusSeconds(secondsUntilExpiration);

        // TODO: validate the number of days. max should be 30?

        Task newTask = Task.builder()
                .label(taskDTO.getLabel())
                .description(taskDTO.getDescription())

                // TODO: Bug here. This needs to be looked at closer. Update tasks keeps resetting time.
                // NOTE: If this works, we can use existing time on update, otherwise need new method.
                // NOTE: Sharing the create method is bad but see if we can use for short term.
                .createdAt(taskDTO.getCreatedAt() != null ? taskDTO.getCreatedAt() : LocalDateTime.now())

                .timerDurationDays(taskDTO.getTimerDurationDays())
                .expirationDate(expirationDate)
                .status(TaskStatus.NEW)
                .build();

        taskRepo.save(newTask);
        saveTasksToFilesystem();
    }

    // method only called via db init, so not need to do filesystem sync here, currently
    @Transactional
    public void createTasks(List<TaskDTO> taskDTOs) {
        List<Task> newTasks = new ArrayList<>();

        // TODO: Promote to streams if possible
        taskDTOs.forEach(taskDTO -> {
            int secondsUntilExpiration = taskDTO.getTimerDurationDays() * SECONDS_IN_24_HOURS;

            // TODO: Revisit this logic, but good enough for this iteration to test live.
            LocalDateTime expirationDate = taskDTO.getCreatedAt() != null
                    ? taskDTO.getCreatedAt().plusSeconds(secondsUntilExpiration)
                    : LocalDateTime.now().plusSeconds(secondsUntilExpiration);

            Task newTask = Task.builder()
                    .label(taskDTO.getLabel())
                    .description(taskDTO.getDescription())

                    // TODO: Bug here. This needs to be looked at closer. Update tasks keeps resetting time.
                    // NOTE: If this works, we can use existing time on update, otherwise need new method.
                    // NOTE: Sharing the create method is bad but see if we can use for short term.
                    .createdAt(taskDTO.getCreatedAt() != null ? taskDTO.getCreatedAt() : LocalDateTime.now())

                    .timerDurationDays(taskDTO.getTimerDurationDays())
                    .expirationDate(expirationDate)
                    .status(TaskStatus.valueOf(taskDTO.getStatus()))
                    .build();

            newTasks.add(newTask);
        });

        taskRepo.saveAll(newTasks);
    }

    @Transactional
    public void completeTaskById(long id) {
        Task task = taskRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        task.setStatus(TaskStatus.COMPLETE);
        task.setCreatedAt(LocalDateTime.now()); // TODO: This is wonky but works for now.

        taskRepo.save(task);
        saveTasksToFilesystem();
    }

    public void completeTaskByLabel(String label) {
        // marks task complete and automatically triggers a scan to update ALL tasks status
    }

    public void deleteTaskById(long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No Task found for id: " + id));

        taskRepo.delete(task);
        saveTasksToFilesystem();
    }

    public void deleteTaskByLabel(String label) {
        Task task = taskRepo.findByLabel(label)
                .orElseThrow(() -> new RuntimeException("No task found for label: " + label));

        taskRepo.delete(task);
        saveTasksToFilesystem();
    }

    public List<Task> getTasks() {
        return taskRepo.findAll();
    }

    public Task getTask(long id) {
        return taskRepo.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void saveTasksToFilesystem() {
        updateAllTaskStatus();

        List<Task> tasks = taskRepo.findAll();

        String tasksJson;
        try {
            tasksJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tasks);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String tasksFilePath = tasksFileUtil.getTasksFile().getPath();

        try {
            Files.writeString(Path.of(tasksFilePath), tasksJson, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to " + tasksFilePath, e);
        }

        log.info("Filesystem save succeeded!");
    }

    @Transactional
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

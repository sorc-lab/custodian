package com.sorclab.custodianserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianserver.Util.TasksFileUtil;
import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.entity.TaskStatus;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.repo.TaskRepo;
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
        LocalDateTime currentTime = LocalDateTime.now();

        int secondsUntilExpiration = taskDTO.getTimerDurationDays() * SECONDS_IN_24_HOURS;
        LocalDateTime expirationDate = currentTime.plusSeconds(secondsUntilExpiration);

        // TODO: validate the number of days. max should be 30?
        // TODO: Add unique constraint on label. Do not update on existing, throw error.

        Task newTask = Task.builder()
                .label(taskDTO.getLabel())
                .description(taskDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .timerDurationDays(taskDTO.getTimerDurationDays())
                .expirationDate(expirationDate)
                .status(TaskStatus.NEW)
                .build();

        taskRepo.save(newTask);
    }

    @Transactional
    public void createTasks(List<TaskDTO> taskDTOs) {
        List<Task> newTasks = new ArrayList<>();

        // TODO: Promote to streams if possible
        taskDTOs.forEach(taskDTO -> {
            // TODO: Consider making private func for this. Duplicate code, see createTask.
            LocalDateTime currentTime = LocalDateTime.now();
            int secondsUntilExpiration = taskDTO.getTimerDurationDays() * SECONDS_IN_24_HOURS;
            LocalDateTime expirationDate = currentTime.plusSeconds(secondsUntilExpiration);

            Task newTask = Task.builder()
                    .label(taskDTO.getLabel())
                    .description(taskDTO.getDescription())
                    .createdAt(LocalDateTime.now())
                    .timerDurationDays(taskDTO.getTimerDurationDays())
                    .expirationDate(expirationDate)
                    .status(TaskStatus.NEW)
                    .build();

            newTasks.add(newTask);
        });

        taskRepo.saveAll(newTasks);
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

    // TODO: Can possibly break up this method. Investigate.
    // TODO: Need check to make sure DB has entities before writing files. Also do a checksum. Don't write new file
    //      every time?
    @Transactional
    public void saveTasksToFilesystem() {
        updateAllTaskStatus(); // TODO: This needs to be carefully tested.

        List<Task> tasks = taskRepo.findAll();
        if (tasks.isEmpty()) {
            log.info("Skipping filesystem save. No Tasks found in memory.");
            return;
        }

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

    // TODO: Consider moving this into its own Schedule vs. hooking this into the save to FS.
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

package com.sorclab.custodianserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.entity.TaskStatus;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.repo.TaskRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private static final int SECONDS_IN_24_HOURS = 86400;
    private static final String TASKS_FILESYSTEM_BK = "../custodian_tasks.json";

    private final TaskRepo taskRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createTask(TaskDTO taskDTO) {
        System.out.println("description" + taskDTO.getDescription());

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



        // TODO: Do not use seconds. Incremental backups vs. archival.
        //      Store file checksum in cache or singleton class. Only write
        //      if new data AND not empty. MUST load in file on startup
        //      before cron task!
        // TODO: Fix json formatting. It's wrapping json array in quotes and adding Windows line
        //      ending chars
        try {
            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("custodian_tasks.json")).getFile());
            //File file = ResourceUtils.getFile("classpath:custodian_tasks.json");

            // TODO: This just doesn't do anything to the file if it has data apparently?
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: On app load, need to read from DB file and load into H2 in-memory DB

    // called every 5mins via cron task and before writing in-memory data to filesystem
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

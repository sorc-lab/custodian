package com.sorclab.custodian.service;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.entity.TaskStatus;
import com.sorclab.custodian.model.TaskDTO;
import com.sorclab.custodian.repo.TaskRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Transactional
    public void createTask(TaskDTO taskDTO) {
        int secondsUntilExpiration = taskDTO.getTimerDurationDays() * SECONDS_IN_24_HOURS;
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(secondsUntilExpiration);

        Task newTask = Task.builder()
                .label(taskDTO.getLabel())
                .description(taskDTO.getDescription())
                .updatedAt(LocalDateTime.now())
                .timerDurationDays(taskDTO.getTimerDurationDays())
                .expirationDate(expirationDate)
                .isComplete(false)
                .build();

        taskRepo.save(newTask);
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
                    // NOTE: This bug may have been fixed by adding a state update call on app start-up, but the
                    //  scheduler and file system integration need to be re-designed.
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
        task.setComplete(true);
        task.setUpdatedAt(LocalDateTime.now());
        task.setExpirationDate(task.getUpdatedAt().plusDays(task.getTimerDurationDays()));

        taskRepo.save(task);
    }

    public void completeTaskByLabel(String label) {
        // marks task complete and automatically triggers a scan to update ALL tasks status
    }

    public void deleteTaskById(long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("No Task found for id: " + id));

        taskRepo.delete(task);
    }

    public void deleteTaskByLabel(String label) {
        Task task = taskRepo.findByLabel(label)
                .orElseThrow(() -> new RuntimeException("No task found for label: " + label));

        taskRepo.delete(task);
    }

    public List<Task> getTasks() {
        return taskRepo.findAll();
    }

    public Task getTask(long id) {
        return taskRepo.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}

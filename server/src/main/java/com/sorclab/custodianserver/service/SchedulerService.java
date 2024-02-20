package com.sorclab.custodianserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
    private final TaskService taskService;

    @Scheduled(initialDelay = 30 * 60 * 1000, fixedRate = 30 * 60 * 1000) // every 30mins
    //@Scheduled(initialDelay = 12 * 60 * 60 * 1000, fixedRate = 12 * 60 * 60 * 1000) // every 12hrs
    public void updateTasksAndSave() {
        log.info("Attempting to update tasks and save to filesystem...");
        taskService.saveTasksToFilesystem();
    }
}

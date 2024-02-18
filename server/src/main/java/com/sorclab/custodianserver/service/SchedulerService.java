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

    // TODO: Consider bumping this up significantly to like once an hour or every 12 hours?
    // NOTE: This can also be run during other REST calls to the system, but maybe unnecessary.
    @SuppressWarnings("unused")
    @Scheduled(fixedRate = 60 * 1000) // Run every 5 minutes
    //@Scheduled(fixedRate = 5 * 60 * 1000) // Run every 5 minutes
    public void updateTasksAndSave() {
        log.info("Attempting to update tasks and save to filesystem...");
        taskService.saveTasksToFilesystem();
    }
}

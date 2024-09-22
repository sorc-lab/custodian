//package com.sorclab.custodian.repo;
//
//import com.sorclab.custodian.util.TasksFileUtil;
//import com.sorclab.custodian.model.TaskDTO;
//import com.sorclab.custodian.service.TaskService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class RepoInitializer implements CommandLineRunner {
//    private final TaskService taskService;
//    private final TasksFileUtil tasksFileUtil;
//
//    @Override
//    public void run(String... args) {
//        // will not overwrite existing data
//        tasksFileUtil.createTasksFile();
//
//        // no need to check if json file exists, will always exist
//        List<TaskDTO> tasks = tasksFileUtil.getTasksFromTasksFile();
//        taskService.createTasks(tasks);
//
//        // TODO: Removing this from init run for now. We will have an empty in-memory DB, this is wiping filesystem data
//        // NOTE: Try to figure out why this was added.
//        // NOTE: This service method should ONLY be called via scheduler. We DO NOT want to update or mess with
//        //  filesystem data on init run
//        //taskService.saveTasksToFilesystem(); // this will update task states.
//    }
//}

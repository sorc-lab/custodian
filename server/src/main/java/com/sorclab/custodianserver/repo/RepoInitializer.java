package com.sorclab.custodianserver.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepoInitializer implements CommandLineRunner {
    private final ObjectMapper objectMapper;
    private final TaskService taskService;

    @Override
    public void run(String... args) {
        // TODO: Handle files better following notes below.
        // check if file exists
        // if not, create it and leave it empty
        // if so, read it into memory and deserialize List<Task>
        //  loop each task and write to database.
        //  NOTE: This seems like it will end up giving new ids every time. Can we prevent this? Do we need to prevent this?

        getTasks().forEach(taskService::createTask);
    }

    private List<TaskDTO> getTasks() {
        List<TaskDTO> tasks;
        try {
            /* TODO: Use the root dir data dir to store the bk file. resources not best practice.
                    read-only dir
            // Specify the path to the root directory's data directory
            String rootDirectoryPath = System.getProperty("user.dir");
            String dataDirectoryPath = rootDirectoryPath + File.separator + "data";

            // Create the data directory if it doesn't exist
            File dataDirectory = new File(dataDirectoryPath);
            if (!dataDirectory.exists()) {
                dataDirectory.mkdirs();  // Create parent directories if needed
            }

            // Construct the file path within the data directory
            File file = new File(dataDirectory, "tasks.json");

            // Your code to convert tasksStr and write to the file goes here
             */


            File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("custodian_tasks.json")).getFile());
            //File file = ResourceUtils.getFile("classpath:custodian_tasks.json");

            log.info(String.valueOf(file.length()));

            // TODO: revisit this concept/handling.
            if (file.length() == 0) {
                return List.of();
            }

            tasks = objectMapper.readValue(file, new TypeReference<List<TaskDTO>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }
}

/*
[
  {
    "id": 1,
    "label": "Kitchen",
    "description": "Mop floors and clean surfaces",
    "createdAt": "2024-02-16T12:23:21.442198",
    "timerDurationDays": 1,
    "status": "NEW",
    "expirationDate": "2024-02-17T12:23:21.442198"
  }
]
 */

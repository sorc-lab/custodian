package com.sorclab.custodianserver.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianserver.model.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TasksFileUtil {
    private static final String TASKS_FILE_NAME = "custodian_tasks.json";

    private final ObjectMapper objectMapper;

    public File getTasksFile() {
        String rootDirPath = System.getProperty("user.dir");
        String dataDirPath = rootDirPath + File.separator + "data";
        String tasksFilePath = dataDirPath + File.separator + TASKS_FILE_NAME;

        return new File(tasksFilePath);
    }

    public void createTasksFile() {
        File tasksFile = getTasksFile();

        // Check if the file already exists
        if (!tasksFile.exists()) {
            // Create the file and its parent directories if they don't exist
            tasksFile.getParentFile().mkdirs();
            try {
                tasksFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + TASKS_FILE_NAME, e);
            }
        }
    }

    public List<TaskDTO> getTasksFromTasksFile() {
        File tasksFile = getTasksFile();

        if (tasksFile.length() == 0) {
            return List.of();
        }

        List<TaskDTO> tasks;
        try {
            tasks = objectMapper.readValue(tasksFile, new TypeReference<List<TaskDTO>>(){});
            //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tasks));
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize " + TASKS_FILE_NAME, e);
        }

        return tasks;
    }
}

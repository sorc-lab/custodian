package com.sorclab.custodianserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorclab.custodianserver.Util.TasksFileUtil;
import com.sorclab.custodianserver.endpoint.TaskController;
import com.sorclab.custodianserver.entity.Task;
import com.sorclab.custodianserver.entity.TaskStatus;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.repo.TaskRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// NOTE: ONLY test nominal paths e2e w/ mock database
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustodianServerApplicationTests {
    @SuppressWarnings("unused")
    @LocalServerPort
    private int port;

    @Autowired private TaskController taskController;
    @Autowired private TestRestTemplate restTemplate;

    @MockBean private TaskRepo taskRepo;
    @MockBean private TasksFileUtil tasksFileUtil;

    @Captor ArgumentCaptor<Task> taskArgCaptor;

    // TODO: Comment out until all happy path unit/integ tests written to not muddy coverage metrics
//    @Test
//    void contextLoads() {
//
//    }

    /* TEST BEHAVIORS
        - Save new Task to DB.
            COVERED
        - Update (expire status) all Task status via DB read and check if any Task expired.
            NOT COVERED
        - IF any tasks are expired, update status and save to database and update in-memory object.
            NOT COVERED
        - Generate list of JSON String data for all Tasks in repo (includes previously written updates)
            NOT COVERED
        - Write JSON data to filesystem based on current repo data previously updated/synced.
            NOT COVERED
     */
    // TODO: Code requires a refactoring to test full nominal flow.
    @Test
    public void createTask() {
        // simulates api input from cli w/ only label, desc, and timer duration in num of days
        TaskDTO taskDTO = TaskDTO.builder()
                .label("test-label")
                .description("test-description")
                .timerDurationDays(7)
                .build();

        // createTask controller endpoint api request
        restTemplate.postForEntity(String.format("http://localhost:%d/task", port), taskDTO, String.class);

        // verify taskRepo persists new Task into in-memory H2 DB
        verify(taskRepo, atLeastOnce()).save(taskArgCaptor.capture());
        assertThat(taskArgCaptor.getValue().getLabel()).isEqualTo("test-label");
        assertThat(taskArgCaptor.getValue().getDescription()).isEqualTo("test-description");
        assertThat(taskArgCaptor.getValue().getCreatedAt().getYear()).isEqualTo(LocalDateTime.now().getYear());
        assertThat(taskArgCaptor.getValue().getCreatedAt().getDayOfMonth()).isEqualTo(LocalDateTime.now().getDayOfMonth());
        verifyExpirationDateCalc(taskArgCaptor.getValue().getExpirationDate(), taskArgCaptor.getValue().getCreatedAt(), taskDTO.getTimerDurationDays());
        assertThat(taskArgCaptor.getValue().getTimerDurationDays()).isEqualTo(7);
        assertThat(taskArgCaptor.getValue().getStatus()).isEqualTo(TaskStatus.NEW);
    }

    // takes calculated expiration date from createTask method and compares to correct calculation
    private void verifyExpirationDateCalc(LocalDateTime calculatedExpDate, LocalDateTime createdAt, int timerDurationDays) {
        int secondsIn24Hours = 86400;
        LocalDateTime expectedExpDate = createdAt.plusSeconds((long) timerDurationDays * secondsIn24Hours);
        assertThat(calculatedExpDate).isEqualTo(expectedExpDate);
    }
}

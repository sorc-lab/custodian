// TODO: Look at shell testing patterns and implement this suite to hit from command shell
// NOTE: Only do nominal workflow tests from shell to a mocked DAL.

//package com.sorclab.custodian;
//
//import com.sorclab.custodian.util.TasksFileUtil;
//import com.sorclab.custodian.endpoint.TaskController;
//import com.sorclab.custodian.entity.Task;
//import com.sorclab.custodian.entity.TaskStatus;
//import com.sorclab.custodian.model.TaskDTO;
//import com.sorclab.custodian.repo.TaskRepo;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//// NOTE: ONLY test nominal paths e2e w/ mock database
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class CustodianApplicationTests {
//    @SuppressWarnings("unused")
//    @LocalServerPort
//    private int port;
//
//    @Autowired private TaskController taskController;
//    @Autowired private TestRestTemplate restTemplate;
//
//    @MockBean private TaskRepo taskRepo;
//    @MockBean private TasksFileUtil tasksFileUtil;
//
//    @Captor ArgumentCaptor<Task> taskArgCaptor;
//
//    // TODO: Comment out until all happy path unit/integ tests written to not muddy coverage metrics
////    @Test
////    void contextLoads() {
////
////    }
//
//    /* TEST BEHAVIORS
//        - Save new Task to DB.
//            COVERED
//        - Update (expire status) all Task status via DB read and check if any Task expired.
//            NOT COVERED
//        - IF any tasks are expired, update status and save to database and update in-memory object.
//            NOT COVERED
//        - Generate list of JSON String data for all Tasks in repo (includes previously written updates)
//            NOT COVERED
//        - Write JSON data to filesystem based on current repo data previously updated/synced.
//            NOT COVERED
//     */
//    // TODO: Code requires a refactoring to test full nominal flow.
//    @Test
//    public void createTask() {
//        // simulates api input from cli w/ only label, desc, and timer duration in num of days
//        TaskDTO taskDTO = TaskDTO.builder()
//                .label("test-label")
//                .description("test-description")
//                .timerDurationDays(7)
//                .build();
//
//        // createTask controller endpoint api request
//        restTemplate.postForEntity(String.format("http://localhost:%d/task", port), taskDTO, String.class);
//
//        // verify taskRepo persists new Task into in-memory H2 DB
//        verify(taskRepo, atLeastOnce()).save(taskArgCaptor.capture());
//        assertThat(taskArgCaptor.getValue().getLabel()).isEqualTo("test-label");
//        assertThat(taskArgCaptor.getValue().getDescription()).isEqualTo("test-description");
//        assertThat(taskArgCaptor.getValue().getCreatedAt().getYear()).isEqualTo(LocalDateTime.now().getYear());
//        assertThat(taskArgCaptor.getValue().getCreatedAt().getDayOfMonth()).isEqualTo(LocalDateTime.now().getDayOfMonth());
//        verifyExpirationDateCalc(taskArgCaptor.getValue().getExpirationDate(), taskArgCaptor.getValue().getCreatedAt(), taskDTO.getTimerDurationDays());
//        assertThat(taskArgCaptor.getValue().getTimerDurationDays()).isEqualTo(7);
//        assertThat(taskArgCaptor.getValue().getStatus()).isEqualTo(TaskStatus.NEW);
//    }
//
//    @Test
//    public void getTasks() {
//        when(taskRepo.findAll()).thenReturn(List.of(Task.builder().build()));
//
//        ResponseEntity<List<Task>> tasks = restTemplate.exchange(
//                String.format("http://localhost:%d/task", port),
//                HttpMethod.GET,
//                new HttpEntity<>(new HttpHeaders()),
//                new ParameterizedTypeReference<List<Task>>(){}
//        );
//
//        assertThat(tasks.getBody()).isEqualTo(List.of(Task.builder().build()));
//    }
//
//    @Test
//    public void getTask() {
//        when(taskRepo.findById(1L)).thenReturn(Optional.of(Task.builder().build()));
//
//        ResponseEntity<Task> task = restTemplate.exchange(
//                String.format("http://localhost:%d/task/%d", port, 1L),
//                HttpMethod.GET,
//                new HttpEntity<>(new HttpHeaders()),
//                Task.class
//        );
//
//        assertThat(task.getBody()).isEqualTo(Task.builder().build());
//    }
//
//    /* TEST BEHAVIORS
//        - find Task by id
//            COVERED
//        - set status to COMPLETE
//            COVERED
//        - reset the createdAt time to LocalDateTime.now(). This effectively re-creates it as new.
//            COVERED
//        - set exp date to NOW plus the Tasks already set timer duration in days.
//            COVERED
//        - persist the updated Task to in-memory H2 DB
//            COVERED
//        - Run an update process on all Tasks in DB to set to expired if true
//            NOT COVERED
//        - Reads all Tasks in in-memory H2 DB and overwrites all Tasks in filesystem json file.
//            NOTE COVERED
//     */
//    @Test
//    public void completeTaskById() {
//        Task task = Task.builder()
//                .id(1L)
//                .label("test-label")
//                .description("test-description")
//                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1))
//                .expirationDate(LocalDateTime.of(2024, 1, 8, 1, 1))
//                .timerDurationDays(7)
//                .status(TaskStatus.COMPLETE)
//                .build();
//
//        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
//
//        restTemplate.exchange(
//                String.format("http://localhost:%d/task/%d", port, 1L),
//                HttpMethod.PUT,
//                new HttpEntity<>(new HttpHeaders()),
//                String.class
//        );
//
//        verify(taskRepo).save(taskArgCaptor.capture());
//        assertThat(taskArgCaptor.getValue().getId()).isEqualTo(1L);
//        assertThat(taskArgCaptor.getValue().getLabel()).isEqualTo("test-label");
//        assertThat(taskArgCaptor.getValue().getDescription()).isEqualTo("test-description");
//        verifyCompleteTaskCreatedAt(taskArgCaptor.getValue().getCreatedAt());
//        verifyCompleteTaskExpirationDate(taskArgCaptor.getValue().getExpirationDate());
//        assertThat(taskArgCaptor.getValue().getTimerDurationDays()).isEqualTo(7);
//        assertThat(taskArgCaptor.getValue().getStatus()).isEqualTo(TaskStatus.COMPLETE);
//    }
//
//    @Test
//    public void deleteTaskById() {
//        when(taskRepo.findById(1L)).thenReturn(Optional.of(Task.builder().id(1L).build()));
//
//        restTemplate.exchange(
//                String.format("http://localhost:%d/task/%d", port, 1L),
//                HttpMethod.DELETE,
//                new HttpEntity<>(new HttpHeaders()),
//                String.class
//        );
//
//        verify(taskRepo).delete(Task.builder().id(1L).build());
//    }
//
//    @Test
//    public void deleteTaskByLabel() {
//        when(taskRepo.findByLabel("test-label")).thenReturn(Optional.of(Task.builder().label("test-label").build()));
//
//        restTemplate.exchange(
//                String.format("http://localhost:%d/task?label=test-label", port),
//                HttpMethod.DELETE,
//                new HttpEntity<>(new HttpHeaders()),
//                String.class
//        );
//
//        verify(taskRepo).delete(Task.builder().label("test-label").build());
//    }
//
//    // takes calculated expiration date from createTask method and compares to correct calculation
//    private void verifyExpirationDateCalc(LocalDateTime calculatedExpDate, LocalDateTime createdAt, int timerDurationDays) {
//        int secondsIn24Hours = 86400;
//        LocalDateTime expectedExpDate = createdAt.plusSeconds((long) timerDurationDays * secondsIn24Hours);
//        assertThat(calculatedExpDate).isEqualTo(expectedExpDate);
//    }
//
//    private void verifyCompleteTaskCreatedAt(LocalDateTime createdAt) {
//        LocalDateTime expectedDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
//        assertThat(createdAt.truncatedTo(ChronoUnit.DAYS)).isEqualTo(expectedDateTime);
//    }
//
//    private void verifyCompleteTaskExpirationDate(LocalDateTime expirationDate) {
//        LocalDateTime expectedExpDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.DAYS);
//        assertThat(expirationDate.truncatedTo(ChronoUnit.DAYS)).isEqualTo(expectedExpDate);
//    }
//}

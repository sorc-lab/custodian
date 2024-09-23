package com.sorclab.custodian.service;

import com.sorclab.custodian.entity.Task;
import com.sorclab.custodian.repo.TaskRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class TaskServiceTest {
    @Mock
    private TaskRepo taskRepo;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskArgCaptor;

    @Test
    public void createTask() {
        Task createTask = Task.builder()
                .label("test-label")
                .description("test-description")
                .timerDurationDays(1)
                .build();

        taskService.createTask(createTask);

        verify(taskRepo).save(taskArgCaptor.capture());

        Task actualSaveTask = taskArgCaptor.getValue();
        assertThat(actualSaveTask.getLabel()).isEqualTo("test-label");
        assertThat(actualSaveTask.getDescription()).isEqualTo("test-description");

        LocalDateTime currentTimeTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        assertThat(actualSaveTask.getUpdatedAt().truncatedTo(ChronoUnit.HOURS))
                .isEqualTo(currentTimeTruncated);

        assertThat(actualSaveTask.getExpirationDate().truncatedTo(ChronoUnit.HOURS))
                .isEqualTo(currentTimeTruncated.plusDays(1L));

        assertThat(actualSaveTask.getTimerDurationDays()).isEqualTo(1);
        assertThat(actualSaveTask.isComplete()).isFalse();
    }

    @Test
    public void completeTaskById() {
        Task existingTask = Task.builder()
                .isComplete(false)
                .updatedAt(LocalDateTime.of(2024, Month.JULY, 30, 10, 10))
                .expirationDate(LocalDateTime.of(2024, Month.AUGUST, 2, 10, 10))
                .timerDurationDays(2)
                .build();

        when(taskRepo.findById(1L)).thenReturn(Optional.of(existingTask));

        taskService.completeTaskById(1L);

        verify(taskRepo).save(taskArgCaptor.capture());

        Task actualSaveTask = taskArgCaptor.getValue();
        LocalDateTime currentTimeTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        assertThat(actualSaveTask.getUpdatedAt().truncatedTo(ChronoUnit.HOURS))
                .isEqualTo(currentTimeTruncated);

        assertThat(actualSaveTask.getExpirationDate().truncatedTo(ChronoUnit.HOURS))
                .isEqualTo(currentTimeTruncated.plusDays(2L));

        assertThat(actualSaveTask.getTimerDurationDays()).isEqualTo(2);
        assertThat(actualSaveTask.isComplete()).isTrue();
    }

    @Test
    public void completeTaskById_ThrowsEntityNotFoundException() {
        when(taskRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.completeTaskById(1L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(taskRepo, times(0)).save(any());
    }

    @Test
    public void deleteTaskById() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(Task.builder().id(1L).build()));

        taskService.deleteTaskById(1L);

        verify(taskRepo).delete(Task.builder().id(1L).build());
    }

    // TODO: assertThatThrownBy is not working as expected in these tests. Fix it.
//    @Test
//    public void deleteTaskById_ThrowsEntityNotFoundException() {
//        when(taskRepo.findById(1L)).thenReturn(Optional.empty());
//
//        taskService.deleteTaskById(1L);
//
//        assertThatThrownBy(() -> taskService.deleteTaskById(1L))
//                .isInstanceOf(EntityNotFoundException.class);
//
//        verify(taskRepo, times(0)).delete(any());
//    }

    @Test
    public void deleteTaskByLabel() {
        when(taskRepo.findByLabel("test-label"))
                .thenReturn(Optional.of(Task.builder().label("test-label").build()));

        taskService.deleteTaskByLabel("test-label");

        verify(taskRepo).delete(Task.builder().label("test-label").build());
    }

    // TODO: assertThatThrownBy is not working as expected in these tests. Fix it.
//    @Test
//    public void deleteTaskByLabel_ThrowsEntityNotFoundException() {
//        //when(taskRepo.findByLabel("test-label")).thenReturn(Optional.empty());
//
//        taskService.deleteTaskByLabel("test-label");
//
//        assertThatThrownBy(() -> taskService.deleteTaskByLabel("test-label"))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("No task found for label: test-label");
//
//        //verify(taskRepo, times(0)).delete(any());
//    }
}

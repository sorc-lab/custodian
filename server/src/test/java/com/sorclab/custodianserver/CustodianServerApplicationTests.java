package com.sorclab.custodianserver;

import com.sorclab.custodianserver.Util.TasksFileUtil;
import com.sorclab.custodianserver.endpoint.TaskController;
import com.sorclab.custodianserver.model.TaskDTO;
import com.sorclab.custodianserver.repo.TaskRepo;
import com.sorclab.custodianserver.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

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

    // TODO: Comment out until all happy path unit/integ tests written to not muddy coverage metrics
//    @Test
//    void contextLoads() {
//
//    }

    @Test
    public void createTask() {
        TaskDTO taskDTO = TaskDTO.builder().build();
        String url = String.format("http://localhost:%d/task", port);

        restTemplate.postForEntity(url, taskDTO, String.class);
    }
}

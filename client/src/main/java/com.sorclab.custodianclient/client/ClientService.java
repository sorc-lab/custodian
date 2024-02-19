package com.sorclab.custodianclient.client;

import com.sorclab.custodianclient.model.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// TODO: All REST calls need to have a response handler that returns to the client proper errors

@Service
@RequiredArgsConstructor
public class ClientService {
    private final RestTemplate restTemplate;

    public void addTask(TaskDTO taskDTO) {
        restTemplate.postForEntity(
                "http://localhost:8080/task",
                taskDTO,
                Object.class
        );
    }

    public List<TaskDTO> getTasks() {
        ResponseEntity<List<TaskDTO>> response = restTemplate.exchange(
                "http://localhost:8080/task",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<TaskDTO>>() {}
        );

        return response.getBody();
    }

    public void deleteTaskById(long id) {
        restTemplate.delete("http://localhost:8080/task/" + id);
    }

    public void deleteTaskByLabel(String label) {
        restTemplate.delete("http://localhost:8080/task?label=" + label);
    }
}

package com.sorclab.custodianserver.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepoInitializer implements CommandLineRunner {
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        // check if file exists
        // if not, create it and leave it empty
        // if so, read it into memory and deserialize List<Task>
        //  loop each task and write to database.
        //  NOTE: This seems like it will end up giving new ids every time. Can we prevent this? Do we need to prevent this?




    }
}

# custodian
Command line interface for scheduling and tracking cleaning tasks.

# Features
- Add remove and edit cleaning tasks.
- Task obj. contains a key, label, description, and timer duration in days.
- Tasks can be added/removed/edited by id or label. Labels must be unique.
- Tasks can be marked as completed, causing the timer to reset.
- Tasks completed, or within a duration window are marked green.
- Tasks that have expired their duration are set to red, and the timer is not reset.
- Tasks that are within a fraction of days until expiry are set to yellow or orange.
- Ability to list tasks and see their colors, labels, and descriptions
- Ability to view an individual task to see its id, label, description and set duration timer.
- Ability to edit any of the fields, except the id.
- CLI home screen will list notifications/alerts. Soon to expire tasks and expired tasks.
- Can also list notifications with its own command to see same thing as printed in home screen alerts.

# Implementation
## Shell Controller
```
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.client.RestTemplate;

@ShellComponent
public class ApiCommand {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiCommand(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @ShellMethod("Make API call to another server")
    public String makeApiCall() {
        String apiUrl = "http://localhost:8081/api/resource"; // replace with your actual API URL
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        return "API Response: " + response.getBody();
    }
}
```

## Shell RestTemplate calls to Backend
```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

## Basic Auth
```
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .and()
            .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("yourUsername")
                .password("{noop}yourPassword") // {noop} indicates plaintext password
                .roles("USER");
    }
}
```

## Backend Controller Custom Header with API Keys
```
@RestController
@RequestMapping("/api")
public class ApiController {

    private final String apiKey = "yourApiKey";

    @GetMapping("/resource")
    public ResponseEntity<String> getResource(@RequestHeader("X-API-Key") String providedApiKey) {
        if (apiKey.equals(providedApiKey)) {
            // Valid API key
            return ResponseEntity.ok("Success");
        } else {
            // Invalid API key
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }
}
```

# Project Structure
## Frontend/Backend Tree
```
my-java-project
├── backend
│   ├── src
│   ├── pom.xml
├── frontend
│   ├── src
│   ├── pom.xml
├── pom.xml (parent)
```

## Parent POM File
```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-java-project</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>backend</module>
        <module>frontend</module>
    </modules>
</project>

```

## Backend Module POM
```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.example</groupId>
        <artifactId>my-java-project</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>

    <artifactId>backend</artifactId>
    <packaging>jar</packaging>

    <!-- Add backend-specific dependencies and configurations here -->
</project>

```

## Frontend Module POM
```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>com.example</groupId>
        <artifactId>my-java-project</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>

    <artifactId>frontend</artifactId>
    <packaging>jar</packaging>

    <!-- Add frontend-specific dependencies and configurations here -->
</project>
```

# Timer
```
@Service
public class YourEntityService {

    @Autowired
    private YourEntityRepository entityRepository;

    public void updateEntityStateBasedOnTimer() {
        List<YourEntity> entities = entityRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (YourEntity entity : entities) {
            LocalDateTime expirationDate = entity.getCreatedAt().plusDays(entity.getTimerDuration());

            if (now.isAfter(expirationDate)) {
                // Update entity state as needed
            }
        }
    }

    public void resetTimer(Long entityId, int newTimerDuration) {
        Optional<YourEntity> optionalEntity = entityRepository.findById(entityId);

        if (optionalEntity.isPresent()) {
            YourEntity entity = optionalEntity.get();
            entity.setTimerDuration(newTimerDuration);
            // Update other fields as needed
            entityRepository.save(entity);
        } else {
            // Handle entity not found
        }
    }
}
```

```
@Service
public class SchedulerService {

    @Autowired
    private YourEntityService entityService;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Run every 24 hours
    public void updateEntityStates() {
        entityService.updateEntityStateBasedOnTimer();
    }
}
```

```
@RestController
@RequestMapping("/api/your-entity")
public class YourEntityController {

    @Autowired
    private YourEntityService entityService;

    @PostMapping("/{entityId}/reset-timer")
    public ResponseEntity<String> resetTimer(@PathVariable Long entityId, @RequestParam int newTimerDuration) {
        entityService.resetTimer(entityId, newTimerDuration);
        return ResponseEntity.ok("Timer reset successfully");
    }
}
```

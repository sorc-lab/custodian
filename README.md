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

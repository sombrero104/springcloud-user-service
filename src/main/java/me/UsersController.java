package me;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UsersController {

    /*private Environment environment;

    @Autowired
    public UsersController(Environment environment) {
        this.environment = environment;
    }*/

    @Autowired
    private Greeting greeting;

    @GetMapping("/health_check")
    public String status() {
        return "It's working in user service.";
    }

    @GetMapping("/welcome")
    public String welcome() {
        // return environment.getProperty("greeting.message");
        return greeting.getMessage();
    }

}

package me.controller;

import me.vo.Greeting;
import me.vo.RequestUser;
import me.dto.UserDto;
import me.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    /*private Environment environment;

    @Autowired
    public UsersController(Environment environment) {
        this.environment = environment;
    }*/

    @Autowired
    private Greeting greeting;

    @Autowired
    private UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return "It's working in user service.";
    }

    @GetMapping("/welcome")
    public String welcome() {
        // return environment.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);
        return "Create user method is called.";
    }

}

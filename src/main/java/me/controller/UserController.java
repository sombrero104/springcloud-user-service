package me.controller;

import me.dto.UserDto;
import me.service.UserService;
import me.vo.Greeting;
import me.vo.RequestUser;
import me.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user-service")
public class UserController {

    /*private Environment environment;

    @Autowired
    public UsersController(Environment environment) {
        this.environment = environment;
    }*/

    private Greeting greeting;
    private UserService userService;

    @Autowired
    public UserController(Greeting greeting, UserService userService) {
        this.greeting = greeting;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status(HttpServletRequest request) {
        return String.format("It's working in user service on port %s", request.getServerPort());
    }

    @GetMapping("/welcome")
    public String welcome() {
        // return environment.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}

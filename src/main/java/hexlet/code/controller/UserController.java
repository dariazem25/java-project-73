package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public User registerNew(@RequestBody @Valid final UserDto dto) {
        return userService.createNewUser(dto);
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    @GetMapping(ID)
    public User getUserById(@PathVariable final Long id) {
        return userService.getUser(id);
    }

    @PutMapping(ID)
    public User update(@PathVariable final Long id, @RequestBody @Valid final UserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        userService.deleteUser(id);
    }
}

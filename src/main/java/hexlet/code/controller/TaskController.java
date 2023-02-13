package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;

import static hexlet.code.controller.TaskController.TASKS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASKS_CONTROLLER_PATH)
public class TaskController {
    public static final String TASKS_CONTROLLER_PATH = "/tasks";

    private static final String ONLY_OWNER_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public Task createTask(@RequestBody @Valid final TaskDto dto) {
        return taskService.createTask(dto);
    }

    @GetMapping
    public List<Task> getAll() {
        return taskRepository.findAll()
                .stream()
                .toList();
    }

    @GetMapping(ID)
    public Task getTaskById(@PathVariable final Long id) {
        return taskService.getTask(id);
    }

    @PutMapping(ID)
    public Task update(@PathVariable final Long id, @RequestBody @Valid final TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void delete(@PathVariable final Long id) {
        taskService.deleteTask(id);
    }
}

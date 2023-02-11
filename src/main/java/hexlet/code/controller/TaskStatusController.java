package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
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

import javax.validation.Valid;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";

    private final TaskStatusService taskStatusService;
    private final TaskStatusRepository taskStatusRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.createTaskStatus(dto);
    }

    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }

    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@PathVariable final Long id) {
        return taskStatusService.getTaskStatus(id);
    }

    @PutMapping(ID)
    public TaskStatus update(@PathVariable final Long id, @RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.updateTaskStatus(id, dto);
    }


    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}

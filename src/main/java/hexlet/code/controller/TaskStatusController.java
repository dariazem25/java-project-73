package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create new task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The task status is created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))})})
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.createTaskStatus(dto);
    }


    @Operation(summary = "Get all task statuses")
    @ApiResponse(responseCode = "200", description = "The task statuses are found",
            content = @Content(schema = @Schema(implementation = TaskStatus.class)))
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }

    @Operation(summary = "Get a task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task status is found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))}),
            @ApiResponse(responseCode = "404", description = "The task status is not found",
                    content = @Content)})
    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@Parameter(description = "id of task status to be searched")
                                            @PathVariable final Long id) {
        return taskStatusService.getTaskStatus(id);
    }

    @Operation(summary = "Update a task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task status is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))}),
            @ApiResponse(responseCode = "404", description = "The task status is not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)})
    @PutMapping(ID)
    public TaskStatus update(@Parameter(description = "id of task status to be updated")
                                 @PathVariable final Long id, @RequestBody @Valid final TaskStatusDto dto) {
        return taskStatusService.updateTaskStatus(id, dto);
    }

    @Operation(summary = "Delete a task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task status is deleted"),
            @ApiResponse(responseCode = "404", description = "The task status is not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Data integrity violation",
                    content = @Content)})
    @DeleteMapping(ID)
    public void delete(@Parameter(description = "id of task status to be deleted")
                           @PathVariable final Long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}

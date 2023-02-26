package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";

    private final LabelService labelService;
    private final LabelRepository labelRepository;

    @Operation(summary = "Create a label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The label is created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class))})})
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto dto) {
        return labelService.createLabel(dto);
    }

    @Operation(summary = "Get all labels")
    @ApiResponse(responseCode = "200", description = "The labels are found",
            content = @Content(schema = @Schema(implementation = Label.class)))
    @GetMapping
    public List<Label> getAll() {
        return labelRepository.findAll()
                .stream()
                .toList();
    }

    @Operation(summary = "Get a label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The label is found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class))})})
    @GetMapping(ID)
    public Label getLabelById(@Parameter(description = "id of label to be searched")
                                  @PathVariable final Long id) {
        return labelService.getLabel(id);
    }

    @Operation(summary = "Update a label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The label is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class))}),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)})
    @PutMapping(ID)
    public Label update(@Parameter(description = "id of label to be updated")
                            @PathVariable final Long id, @RequestBody @Valid final LabelDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @Operation(summary = "Delete a label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The label is deleted"),
            @ApiResponse(responseCode = "422", description = "The task is not found",
                    content = @Content)})
    @DeleteMapping(ID)
    public void delete(@Parameter(description = "Data integrity violation")
                           @PathVariable final Long id) {
        labelService.deleteLabel(id);
    }
}

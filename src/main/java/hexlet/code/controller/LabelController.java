package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
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

    @PostMapping
    @ResponseStatus(CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto dto) {
        return labelService.createLabel(dto);
    }

    @GetMapping
    public List<Label> getAll() {
        return labelRepository.findAll()
                .stream()
                .toList();
    }

    @GetMapping(ID)
    public Label getLabelById(@PathVariable final Long id) {
        return labelService.getLabel(id);
    }

    @PutMapping(ID)
    public Label update(@PathVariable final Long id, @RequestBody @Valid final LabelDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final Long id) {
        labelService.deleteLabel(id);
    }
}

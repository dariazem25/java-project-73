package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public Label createLabel(LabelDto labelDto) {
        final Label label = new Label();
        label.setName(labelDto.getName());

        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(Long id, LabelDto labelDto) {
        final Label labelToUpdate = labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label not found"));

        labelToUpdate.setName(labelDto.getName());

        return labelRepository.save(labelToUpdate);
    }

    @Override
    public Label getLabel(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label not found"));
    }

    @Override
    public void deleteLabel(Long id) {
        final Label label = labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label not found"));
        if (!label.getTasks().isEmpty()) {
            throw new DataIntegrityViolationException("Cannot delete the label. Tasks have labels");
        }
        labelRepository.delete(label);
    }
}

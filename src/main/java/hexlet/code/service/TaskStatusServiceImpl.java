package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    @Override
    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task status not found"));
        taskStatusToUpdate.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }

    @Override
    public TaskStatus getTaskStatus(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task status not found"));
    }

    @Override
    public void deleteTaskStatus(Long id) {
        final TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task status not found"));
        if (!taskStatus.getTasks().isEmpty()) {
            throw new DataIntegrityViolationException("Cannot delete the task status. The task status has tasks");
        }
        taskStatusRepository.delete(taskStatus);
    }
}

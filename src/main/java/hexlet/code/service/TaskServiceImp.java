package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.exceptions.InvalidRequestException;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    @Override
    public Task createTask(TaskDto taskDto) {
        final Task task = new Task();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setAuthor(userService.getCurrentUser());

        if (taskDto.getExecutorId() != null) {
            var user = userRepository.findById(taskDto.getExecutorId())
                    .orElseThrow(() -> new InvalidRequestException("Executor not found"));
            task.setExecutor(user);
        }

        var taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId())
                .orElseThrow(() -> new InvalidRequestException("Task status not found"));
        task.setTaskStatus(taskStatus);

        if (taskDto.getLabelsIds() != null) {
            taskDto.getLabelsIds().forEach(
                    it -> labelRepository.findById(it)
                            .orElseThrow(() -> new InvalidRequestException("Label not found"))
            );

            task.setLabels(taskDto.getLabelsIds().stream()
                    .map(labelRepository::getById)
                    .collect(Collectors.toSet()));
        }

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        final Task taskToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestException("Task not found"));

        taskToUpdate.setName(taskDto.getName());
        taskToUpdate.setDescription(taskDto.getDescription());

        if (taskDto.getExecutorId() != null) {
            var user = userRepository.findById(taskDto.getExecutorId())
                    .orElseThrow(() -> new InvalidRequestException("Executor not found"));
            taskToUpdate.setExecutor(user);
        }

        var taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId())
                .orElseThrow(() -> new InvalidRequestException("Task status not found"));
        taskToUpdate.setTaskStatus(taskStatus);

        taskToUpdate.setLabels(null);
        if (taskDto.getLabelsIds() != null) {
            taskDto.getLabelsIds().forEach(
                    it -> labelRepository.findById(it)
                            .orElseThrow(() -> new InvalidRequestException("Label not found"))
            );
            taskToUpdate.setLabels(taskDto.getLabelsIds().stream()
                    .map(labelRepository::getById)
                    .collect(Collectors.toSet()));
        }

        return taskRepository.save(taskToUpdate);
    }

    @Override
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
    }

    @Override
    public void deleteTask(Long id) {
        final Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found"));
        taskRepository.delete(task);

    }
}

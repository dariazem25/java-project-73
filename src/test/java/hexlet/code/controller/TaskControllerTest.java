package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class TaskControllerTest {

    @Autowired
    private TestUtils utils;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createValidTask() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final Task expectedTask = taskRepository.findById(actualTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertNull(expectedTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertNull(actualTask.getExecutor());

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertNull(actualTask.getLabels());
    }

    @Test
    public void createValidTaskWithDefinedDescription() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", "Description", null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final Task expectedTask = taskRepository.findById(actualTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertNull(actualTask.getExecutor());

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertNull(actualTask.getLabels());
    }

    @Test
    public void createValidTaskWithDefinedExecutorId() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, executorId, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final Task expectedTask = taskRepository.findById(actualTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertNull(expectedTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertEquals(expectedTask.getExecutor().getEmail(), actualTask.getExecutor().getEmail());
        assertEquals(expectedTask.getExecutor().getFirstName(), actualTask.getExecutor().getFirstName());
        assertEquals(expectedTask.getExecutor().getLastName(), actualTask.getExecutor().getLastName());
        assertThat(actualTask.getExecutor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertNull(actualTask.getLabels());
    }

    @Test
    public void createValidTaskWithDefinedLabel() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // existing label
        final Label label = utils.createDefaultLabel(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null, taskStatus.getId(), Set.of(label.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final Task expectedTask = taskRepository.findById(actualTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertNull(actualTask.getExecutor());

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getLabels().stream().toList().get(0).getName(),
                actualTask.getLabels().stream().toList().get(0).getName());
        assertEquals(expectedTask.getLabels().stream().toList().get(0).getId(),
                actualTask.getLabels().stream().toList().get(0).getId());
    }

    @Test
    public void createInvalidTaskWithEmptyName() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("", null, null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void createInvalidTaskWithMissingName() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto(null, null, null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void createInvalidTaskWithMissingTaskStatusId() throws Exception {
        utils.regDefaultUser();

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null, null, null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be null\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"taskStatusId\"");
    }

    @Test
    public void createInvalidTaskWithNonExistentTaskStatus() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null, Long.MAX_VALUE, null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("Task status not found");
    }

    @Test
    public void createInvalidTaskWithNonExistentExecutorId() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, Long.MAX_VALUE, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("Executor not found");
    }

    @Test
    public void createInvalidTaskWithNonExistentLabel() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        final var response = utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null,
                                taskStatus.getId(), Set.of(Long.MAX_VALUE))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("Label not found");
    }

    @Test
    public void updateTask() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", null,
                                null, oldTask.getTaskStatus().getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Task expectedTask = taskRepository.findById(oldTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertNull(actualTask.getExecutor());

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertNull(actualTask.getLabels());
    }

    @Test
    public void updateToValidTaskWithDefinedDescription() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", "New Description",
                                null, oldTask.getTaskStatus().getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Task expectedTask = taskRepository.findById(oldTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertNull(actualTask.getExecutor());

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertNull(actualTask.getLabels());
    }

    @Test
    public void updateToValidTaskWithDefinedExecutorId() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name",
                                null, executorId, oldTask.getTaskStatus().getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Task expectedTask = taskRepository.findById(oldTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertNull(actualTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertEquals(expectedTask.getExecutor().getEmail(), actualTask.getExecutor().getEmail());
        assertEquals(expectedTask.getExecutor().getFirstName(), actualTask.getExecutor().getFirstName());
        assertEquals(expectedTask.getExecutor().getLastName(), actualTask.getExecutor().getLastName());
        assertThat(actualTask.getExecutor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertNull(actualTask.getLabels());
    }

    @Test
    public void updateToValidTaskWithDefinedLabel() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // existing label
        final Label label = utils.createDefaultLabel(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", null,
                                null, oldTask.getTaskStatus().getId(), Set.of(label.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Task expectedTask = taskRepository.findById(oldTask.getId()).get();

        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
        assertNull(actualTask.getDescription());
        assertThat(actualTask.getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(expectedTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(expectedTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(actualTask.getAuthor().getCreatedAt()).isNotNull();

        assertNull(actualTask.getExecutor());

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getLabels().stream().toList().get(0).getName(),
                actualTask.getLabels().stream().toList().get(0).getName());
        assertEquals(expectedTask.getLabels().stream().toList().get(0).getId(),
                actualTask.getLabels().stream().toList().get(0).getId());
    }

    @Test
    public void updateToInvalidTaskWithEmptyName() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("", null,
                                null, oldTask.getTaskStatus().getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void updateToInvalidTaskWithMissingName() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto(null, null,
                                null, oldTask.getTaskStatus().getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void updateToInvalidTaskWithMissingTaskStatusId() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", null,
                                null, null, null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be null\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"taskStatusId\"");
    }

    @Test
    public void updateToInvalidTaskWithNonExistentTaskStatus() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASK_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", null,
                                null, Long.MAX_VALUE, null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task status not found");
    }

    @Test
    public void getAllTasks() throws Exception {
        utils.regDefaultUser();

        // existing task
        final Task existingTask = utils.createDefaultTask(TEST_USERNAME);

        // get tasks
        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(existingTask.getId()).isEqualTo(tasks.get(0).getId());
        assertNull(tasks.get(0).getDescription());
        assertThat(existingTask.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(existingTask.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(existingTask.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(existingTask.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(existingTask.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(existingTask.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(existingTask.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertNull(tasks.get(0).getExecutor());

        assertEquals(existingTask.getTaskStatus().getId(), tasks.get(0).getTaskStatus().getId());
        assertEquals(existingTask.getTaskStatus().getName(), tasks.get(0).getTaskStatus().getName());
        assertThat(existingTask.getTaskStatus().getCreatedAt()).isEqualTo(tasks.get(0).getTaskStatus().getCreatedAt());
    }

    @Test
    public void getTaskById() throws Exception {
        utils.regDefaultUser();

        // existing task
        final Task existingTask = utils.createDefaultTask(TEST_USERNAME);

        // get task by id
        final var response = utils.perform(
                        get(BASE_URL + TASK_CONTROLLER_PATH + ID, existingTask.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(existingTask.getId()).isEqualTo(actualTask.getId());
        assertNull(actualTask.getDescription());
        assertThat(existingTask.getCreatedAt()).isEqualTo(actualTask.getCreatedAt());
        assertThat(existingTask.getName()).isEqualTo(actualTask.getName());

        assertEquals(existingTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(existingTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(existingTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(existingTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(existingTask.getAuthor().getCreatedAt()).isEqualTo(actualTask.getAuthor().getCreatedAt());

        assertNull(actualTask.getExecutor());

        assertEquals(existingTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(existingTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(existingTask.getTaskStatus().getCreatedAt()).isEqualTo(actualTask.getTaskStatus().getCreatedAt());
    }

    @Test
    public void getTaskByNonExistentId() throws Exception {
        utils.regDefaultUser();

        // get task by non existent id
        final var response = utils.perform(
                        get(BASE_URL + TASK_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task not found");
    }

    @Test
    public void deleteTask() throws Exception {
        utils.regDefaultUser();

        // existing task
        final Task existingTask = utils.createDefaultTask(TEST_USERNAME);

        // delete the task
        final var response = utils.perform(delete(BASE_URL + TASK_CONTROLLER_PATH + ID,
                        existingTask.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();

        // the task is deleted
        assertEquals(0, taskRepository.count());
    }

    @Test
    public void deleteTaskByNonExistentId() throws Exception {
        utils.regDefaultUser();

        // delete by non existent task
        final var response = utils.perform(delete(BASE_URL + TASK_CONTROLLER_PATH + ID,
                        1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    public void deleteTaskByAnotherUser() throws Exception {
        utils.regDefaultUser();

        // existing task
        final Task existingTask = utils.createDefaultTask(TEST_USERNAME);

        //another user
        final var response1 = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(asJson(new UserDto(TEST_USERNAME_2, "First", "Last", "123")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final User user2 = fromJson(response1.getContentAsString(), new TypeReference<>() {
        });

        // delete by another user
        final var response2 = utils.perform(delete(BASE_URL + TASK_CONTROLLER_PATH + ID,
                        existingTask.getId()), TEST_USERNAME_2)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    public void getTasksWithDefinedTaskStatus() throws Exception {

        // Preconditions:
        utils.regDefaultUser();

        // 1) the first task with task status
        final Task task1 = utils.createDefaultTask(TEST_USERNAME);

        // 2) the second task with another task status
        final TaskStatus taskStatus = fromJson(utils.perform(post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(new TaskStatusDto("Fixed")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });
        final Task task2 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Actions:
        // Get tasks
        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH
                        + "?taskStatus=" + taskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(task2.getId()).isEqualTo(tasks.get(0).getId());
        assertNull(tasks.get(0).getDescription());
        assertThat(task2.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(task2.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(task2.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(task2.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(task2.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(task2.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(task2.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertNull(tasks.get(0).getExecutor());

        assertEquals(task2.getTaskStatus().getId(), tasks.get(0).getTaskStatus().getId());
        assertEquals(task2.getTaskStatus().getName(), tasks.get(0).getTaskStatus().getName());
        assertThat(task2.getTaskStatus().getCreatedAt()).isEqualTo(tasks.get(0).getTaskStatus().getCreatedAt());
    }

    @Test
    public void getTasksWithDefinedExecutorId() throws Exception {

        // Preconditions:
        // one user
        utils.regDefaultUser();
        // another user
        User user2 = fromJson(utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(asJson(new UserDto("d@mail.ru", "D", "Z", "123")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });


        // default task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);
        final Long executorId1 = userRepository.findByEmail(TEST_USERNAME).get().getId();

        // the first task with executor id
        final Task task1 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, executorId1, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // the second task with another executor id
        final Long executorId2 = user2.getId();
        final Task task2 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Second task", null, executorId2, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Actions:
        // Get tasks
        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH
                        + "?executorId=" + executorId1), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(task1.getId()).isEqualTo(tasks.get(0).getId());
        assertNull(tasks.get(0).getDescription());
        assertThat(task1.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(task1.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(task1.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(task1.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(task1.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(task1.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(task1.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertEquals(task1.getExecutor().getId(), tasks.get(0).getExecutor().getId());
        assertEquals(task1.getExecutor().getEmail(), tasks.get(0).getExecutor().getEmail());
        assertEquals(task1.getExecutor().getFirstName(), tasks.get(0).getExecutor().getFirstName());
        assertEquals(task1.getExecutor().getLastName(), tasks.get(0).getExecutor().getLastName());
        assertThat(tasks.get(0).getExecutor().getCreatedAt()).isNotNull();

        assertEquals(task1.getTaskStatus().getId(), tasks.get(0).getTaskStatus().getId());
        assertEquals(task1.getTaskStatus().getName(), tasks.get(0).getTaskStatus().getName());
        assertThat(task1.getTaskStatus().getCreatedAt()).isEqualTo(tasks.get(0).getTaskStatus().getCreatedAt());
    }

    @Test
    public void getTasksWithDefinedLabel() throws Exception {

        // Preconditions:
        // user
        utils.regDefaultUser();

        // default task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // one label
        Label label1 = utils.createDefaultLabel(TEST_USERNAME);

        // another label
        final Label label2 = fromJson(utils.perform(post(BASE_URL + LABEL_CONTROLLER_PATH)
                        .content(asJson(new LabelDto("Bug")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // the first task with label
        final Task task1 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null,
                                null, taskStatus.getId(), Set.of(label1.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // the second task with another label
        final Task task2 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Second task", null,
                                null, taskStatus.getId(), Set.of(label2.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Actions:
        // Get tasks
        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH
                        + "?labels=" + label1.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(task1.getId()).isEqualTo(tasks.get(0).getId());
        assertNull(tasks.get(0).getDescription());
        assertThat(task1.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(task1.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(task1.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(task1.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(task1.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(task1.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(task1.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertNull(tasks.get(0).getExecutor());

        assertEquals(task1.getTaskStatus().getId(), tasks.get(0).getTaskStatus().getId());
        assertEquals(task1.getTaskStatus().getName(), tasks.get(0).getTaskStatus().getName());
        assertThat(task1.getTaskStatus().getCreatedAt()).isEqualTo(tasks.get(0).getTaskStatus().getCreatedAt());

        assertEquals(task1.getLabels().stream().toList().get(0).getName(),
                tasks.get(0).getLabels().stream().toList().get(0).getName());
        assertEquals(task1.getLabels().stream().toList().get(0).getId(),
                tasks.get(0).getLabels().stream().toList().get(0).getId());
    }

    @Test
    public void getTasksWithDefinedAuthorId() throws Exception {

        // Preconditions:
        // one user
        utils.regDefaultUser();
        // another user
        User user2 = fromJson(utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(asJson(new UserDto("d@mail.ru", "D", "Z", "123")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });


        // default task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // the first task with the first author
        final Task task1 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null,
                                null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // the second task with another author
        final Task task2 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Second task", null,
                                null, taskStatus.getId(), null)))
                        .contentType(APPLICATION_JSON), user2.getEmail())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Actions:
        // Get tasks
        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH
                        + "?authorId=" + user2.getId()), user2.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(task2.getId()).isEqualTo(tasks.get(0).getId());
        assertNull(tasks.get(0).getDescription());
        assertThat(task2.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(task2.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(task2.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(task2.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(task2.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(task2.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(task2.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertNull(tasks.get(0).getExecutor());

        assertEquals(task2.getTaskStatus().getId(), tasks.get(0).getTaskStatus().getId());
        assertEquals(task2.getTaskStatus().getName(), tasks.get(0).getTaskStatus().getName());
        assertThat(task2.getTaskStatus().getCreatedAt()).isEqualTo(tasks.get(0).getTaskStatus().getCreatedAt());
    }

    @Test
    public void getTasksWithAllFilters() throws Exception {

        // Preconditions:
        // users
        utils.regDefaultUser();
        final User user2 = fromJson(utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(asJson(new UserDto("d@mail.ru", "D", "Z", "123")))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // labels
        final Label label1 = utils.createDefaultLabel(TEST_USERNAME);
        final Label label2 = fromJson(utils.perform(post(BASE_URL + LABEL_CONTROLLER_PATH)
                        .content(asJson(new LabelDto("Bug")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // task statuses
        final TaskStatus taskStatus1 = fromJson(utils.perform(post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(new TaskStatusDto("In development")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });
        final TaskStatus taskStatus2 = fromJson(utils.perform(post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(new TaskStatusDto("Fixed")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        //executors ids
        final Long executorId1 = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final Long executorId2 = user2.getId();

        // tasks
        final Task task1 = utils.createDefaultTask(TEST_USERNAME);
        final Task task2 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Second task", null,
                                executorId1, taskStatus1.getId(), Set.of(label1.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });
        final Task task3 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Third task", null,
                                executorId1, taskStatus1.getId(), Set.of(label2.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });
        final Task task4 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Fourth task", null,
                                executorId2, taskStatus1.getId(), Set.of(label1.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });
        final Task task5 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Fifth task", null,
                                executorId1, taskStatus2.getId(), Set.of(label1.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        final Task task6 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Sixth task", null,
                                executorId1, taskStatus1.getId(), Set.of(label1.getId()))))
                        .contentType(APPLICATION_JSON), user2.getEmail())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        final Task task7 = fromJson(utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("Seventh task", null,
                                executorId2, taskStatus2.getId(), Set.of(label2.getId()))))
                        .contentType(APPLICATION_JSON), user2.getEmail())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString(), new TypeReference<>() {
        });

        // Actions:
        // Get tasks
        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH
                                + "?labels=" + label2.getId()
                                + "&executorId=" + executorId2
                                + "&taskStatus=" + taskStatus2.getId()
                                + "&authorId=" + user2.getId()),
                        user2.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(task7.getId()).isEqualTo(tasks.get(0).getId());
        assertNull(tasks.get(0).getDescription());
        assertThat(task7.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(task7.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(task7.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(task7.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(task7.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(task7.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(task7.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertEquals(task7.getExecutor().getId(), tasks.get(0).getExecutor().getId());
        assertEquals(task7.getExecutor().getEmail(), tasks.get(0).getExecutor().getEmail());
        assertEquals(task7.getExecutor().getFirstName(), tasks.get(0).getExecutor().getFirstName());
        assertEquals(task7.getExecutor().getLastName(), tasks.get(0).getExecutor().getLastName());
        assertThat(tasks.get(0).getExecutor().getCreatedAt()).isNotNull();

        assertEquals(task7.getTaskStatus().getId(), tasks.get(0).getTaskStatus().getId());
        assertEquals(task7.getTaskStatus().getName(), tasks.get(0).getTaskStatus().getName());
        assertThat(task7.getTaskStatus().getCreatedAt()).isEqualTo(tasks.get(0).getTaskStatus().getCreatedAt());
    }
}

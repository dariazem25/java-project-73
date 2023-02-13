package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.UserDto;
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

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASKS_CONTROLLER_PATH;
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

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASKS_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", "Description", executorId, taskStatus.getId())))
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

        assertEquals(expectedTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertEquals(expectedTask.getExecutor().getEmail(), actualTask.getExecutor().getEmail());
        assertEquals(expectedTask.getExecutor().getFirstName(), actualTask.getExecutor().getFirstName());
        assertEquals(expectedTask.getExecutor().getLastName(), actualTask.getExecutor().getLastName());
        assertThat(actualTask.getExecutor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();
    }

    @Test
    public void createValidTaskWithMissingDescription() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASKS_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, executorId, taskStatus.getId())))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final Task expectedTask = taskRepository.findById(actualTask.getId()).get();

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
    }

    @Test
    public void createValidTaskWithMissingExecutorId() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASKS_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", "Description", null, taskStatus.getId())))
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
    }

    @Test
    public void createInvalidTaskWithEmptyName() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASKS_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("", "Description", executorId, taskStatus.getId())))
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

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASKS_CONTROLLER_PATH)
                        .content(asJson(new TaskDto(null, "Description", executorId, taskStatus.getId())))
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

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // executor id
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(post(BASE_URL + TASKS_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", "Description", executorId, null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, taskRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be null\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"taskStatusId\"");
    }

    @Test
    public void updateTask() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASKS_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", "new description",
                                oldTask.getExecutor().getId(), oldTask.getTaskStatus().getId())))
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

        assertEquals(expectedTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertEquals(expectedTask.getExecutor().getEmail(), actualTask.getExecutor().getEmail());
        assertEquals(expectedTask.getExecutor().getFirstName(), actualTask.getExecutor().getFirstName());
        assertEquals(expectedTask.getExecutor().getLastName(), actualTask.getExecutor().getLastName());
        assertThat(actualTask.getExecutor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();
    }

    @Test
    public void updateToValidTaskWithMissingDescription() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASKS_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", null,
                                oldTask.getExecutor().getId(), oldTask.getTaskStatus().getId())))
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
    }

    @Test
    public void updateToValidTaskWithMissingExecutorId() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASKS_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name",
                                "new description", null, oldTask.getTaskStatus().getId())))
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

        assertEquals(oldTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertEquals(oldTask.getExecutor().getEmail(), actualTask.getExecutor().getEmail());
        assertEquals(oldTask.getExecutor().getFirstName(), actualTask.getExecutor().getFirstName());
        assertEquals(oldTask.getExecutor().getLastName(), actualTask.getExecutor().getLastName());
        assertThat(oldTask.getExecutor().getCreatedAt()).isNotNull();

        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(expectedTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(actualTask.getTaskStatus().getCreatedAt()).isNotNull();
    }

    @Test
    public void updateToInvalidTaskWithEmptyName() throws Exception {
        utils.regDefaultUser();

        // created task
        final Task oldTask = utils.createDefaultTask(TEST_USERNAME);

        // updated task
        final var response = utils.perform(put(BASE_URL + TASKS_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("", "new description",
                                oldTask.getExecutor().getId(), oldTask.getTaskStatus().getId())))
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
        final var response = utils.perform(put(BASE_URL + TASKS_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto(null, "new description",
                                oldTask.getExecutor().getId(), oldTask.getTaskStatus().getId())))
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
        final var response = utils.perform(put(BASE_URL + TASKS_CONTROLLER_PATH + ID, oldTask.getId())
                        .content(asJson(new TaskDto("New task name", "new description",
                                oldTask.getExecutor().getId(), null)))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be null\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"taskStatusId\"");
    }

    @Test
    public void getAllTasks() throws Exception {
        utils.regDefaultUser();

        // existing task
        final Task existingTask = utils.createDefaultTask(TEST_USERNAME);

        // get tasks
        final var response = utils.perform(get(BASE_URL + TASKS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        assertThat(existingTask.getId()).isEqualTo(tasks.get(0).getId());
        assertEquals(existingTask.getDescription(), tasks.get(0).getDescription());
        assertThat(existingTask.getCreatedAt()).isEqualTo(tasks.get(0).getCreatedAt());
        assertThat(existingTask.getName()).isEqualTo(tasks.get(0).getName());

        assertEquals(existingTask.getAuthor().getId(), tasks.get(0).getAuthor().getId());
        assertEquals(existingTask.getAuthor().getEmail(), tasks.get(0).getAuthor().getEmail());
        assertEquals(existingTask.getAuthor().getFirstName(), tasks.get(0).getAuthor().getFirstName());
        assertEquals(existingTask.getAuthor().getLastName(), tasks.get(0).getAuthor().getLastName());
        assertThat(existingTask.getAuthor().getCreatedAt()).isEqualTo(tasks.get(0).getAuthor().getCreatedAt());

        assertEquals(existingTask.getExecutor().getId(), tasks.get(0).getExecutor().getId());
        assertEquals(existingTask.getExecutor().getEmail(), tasks.get(0).getExecutor().getEmail());
        assertEquals(existingTask.getExecutor().getFirstName(), tasks.get(0).getExecutor().getFirstName());
        assertEquals(existingTask.getExecutor().getLastName(), tasks.get(0).getExecutor().getLastName());
        assertThat(existingTask.getExecutor().getCreatedAt()).isEqualTo(tasks.get(0).getExecutor().getCreatedAt());

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
                        get(BASE_URL + TASKS_CONTROLLER_PATH + ID, existingTask.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(existingTask.getId()).isEqualTo(actualTask.getId());
        assertEquals(existingTask.getDescription(), actualTask.getDescription());
        assertThat(existingTask.getCreatedAt()).isEqualTo(actualTask.getCreatedAt());
        assertThat(existingTask.getName()).isEqualTo(actualTask.getName());

        assertEquals(existingTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(existingTask.getAuthor().getEmail(), actualTask.getAuthor().getEmail());
        assertEquals(existingTask.getAuthor().getFirstName(), actualTask.getAuthor().getFirstName());
        assertEquals(existingTask.getAuthor().getLastName(), actualTask.getAuthor().getLastName());
        assertThat(existingTask.getAuthor().getCreatedAt()).isEqualTo(actualTask.getAuthor().getCreatedAt());

        assertEquals(existingTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertEquals(existingTask.getExecutor().getEmail(), actualTask.getExecutor().getEmail());
        assertEquals(existingTask.getExecutor().getFirstName(), actualTask.getExecutor().getFirstName());
        assertEquals(existingTask.getExecutor().getLastName(), actualTask.getExecutor().getLastName());
        assertThat(existingTask.getExecutor().getCreatedAt()).isEqualTo(actualTask.getExecutor().getCreatedAt());

        assertEquals(existingTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(existingTask.getTaskStatus().getName(), actualTask.getTaskStatus().getName());
        assertThat(existingTask.getTaskStatus().getCreatedAt()).isEqualTo(actualTask.getTaskStatus().getCreatedAt());
    }

    @Test
    public void getTaskByNonExistentId() throws Exception {
        utils.regDefaultUser();

        // get task by non existent id
        final var response = utils.perform(
                        get(BASE_URL + TASKS_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
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
        final var response = utils.perform(delete(BASE_URL + TASKS_CONTROLLER_PATH + ID,
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
        final var response = utils.perform(delete(BASE_URL + TASKS_CONTROLLER_PATH + ID,
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
        final var response2 = utils.perform(delete(BASE_URL + TASKS_CONTROLLER_PATH + ID,
                        existingTask.getId()), TEST_USERNAME_2)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }
}

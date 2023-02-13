package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class TaskStatusControllerTest {

    @Autowired
    private TestUtils utils;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createValidTaskStatus() throws Exception {
        utils.regDefaultUser();

        final var response = utils.perform(post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(new TaskStatusDto("New")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus actualTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final TaskStatus expectedTaskStatus = taskStatusRepository.findById(actualTaskStatus.getId()).get();

        assertEquals(expectedTaskStatus.getId(), actualTaskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), actualTaskStatus.getName());
        assertThat(actualTaskStatus.getCreatedAt()).isNotNull();

        assertThat(response.getContentAsString()).contains("name");
        assertThat(response.getContentAsString()).contains("id");
        assertThat(response.getContentAsString()).contains("createdAt");
    }

    @Test
    public void createInvalidTaskStatus() throws Exception {
        utils.regDefaultUser();

        final var response = utils.perform(post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(new TaskStatusDto("")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, taskStatusRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void updateTaskStatus() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus oldTaskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // updated task status
        final var response = utils.perform(put(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, oldTaskStatus.getId())
                        .content(asJson(new TaskStatusDto("New status")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus actualTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final TaskStatus expectedTaskStatus = taskStatusRepository.findById(oldTaskStatus.getId()).get();

        assertEquals(expectedTaskStatus.getId(), actualTaskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), actualTaskStatus.getName());
        assertThat(actualTaskStatus.getCreatedAt()).isNotNull();

        assertThat(response.getContentAsString()).contains("name");
        assertThat(response.getContentAsString()).contains("id");
        assertThat(response.getContentAsString()).contains("createdAt");
    }

    @Test
    public void updateToInvalidTaskStatus() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus oldTaskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // update task status
        final var response = utils.perform(put(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, oldTaskStatus.getId())
                        .content(asJson(new TaskStatusDto("")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = taskStatusRepository.findById(oldTaskStatus.getId()).get();

        // the task status was not updated
        assertEquals(oldTaskStatus.getId(), taskStatus.getId());
        assertEquals(oldTaskStatus.getName(), taskStatus.getName());
        assertThat(oldTaskStatus.getCreatedAt()).isNotNull();

        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void updateNonExistentTaskStatus() throws Exception {
        utils.regDefaultUser();

        // update non existent task status
        final var response = utils.perform(put(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, 1)
                        .content(asJson(new TaskStatusDto("New")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task status not found");
    }

    @Test
    public void getAllTaskStatuses() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus existingTaskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // get task statuses
        final var response = utils.perform(get(BASE_URL + TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(taskStatuses).hasSize(1);

        assertThat(existingTaskStatus.getId()).isEqualTo(taskStatuses.get(0).getId());
        assertThat(existingTaskStatus.getCreatedAt()).isEqualTo(taskStatuses.get(0).getCreatedAt());
        assertThat(existingTaskStatus.getName()).isEqualTo(taskStatuses.get(0).getName());
    }

    @Test
    public void getTaskStatusById() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus existingTaskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // get task status by id
        final var response = utils.perform(
                        get(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, existingTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus actualTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(existingTaskStatus.getId(), actualTaskStatus.getId());
        assertEquals(existingTaskStatus.getCreatedAt(), actualTaskStatus.getCreatedAt());
        assertEquals(existingTaskStatus.getName(), actualTaskStatus.getName());
    }

    @Test
    public void getTaskStatusByNonExistentId() throws Exception {
        utils.regDefaultUser();

        // get task status by non existent id
        final var response = utils.perform(
                        get(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task status not found");
    }

    @Test
    public void deleteTaskStatus() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus existingTaskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // delete the task status
        final var response = utils.perform(delete(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID,
                        existingTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();

        // the task status is deleted
        assertEquals(0, taskStatusRepository.count());
    }

    @Test
    public void deleteNonExistentTaskStatus() throws Exception {
        utils.regDefaultUser();

        // delete non existent task
        final var response = utils.perform(delete(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Task status not found");
    }

    @Test
    public void deleteTaskStatusWithExistingTasks() throws Exception {
        utils.regDefaultUser();

        // created task with task status
        final Task task = utils.createDefaultTask(TEST_USERNAME);

        // delete task
        final var response = utils.perform(delete(BASE_URL + TASK_STATUS_CONTROLLER_PATH + ID,
                        task.getTaskStatus().getId()), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Cannot delete the task status. The task status has tasks");

        assertEquals(1, taskStatusRepository.count());
    }
}

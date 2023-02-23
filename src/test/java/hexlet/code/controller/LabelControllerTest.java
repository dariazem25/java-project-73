package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
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
public class LabelControllerTest {

    @Autowired
    private TestUtils utils;

    @Autowired
    private LabelRepository labelRepository;


    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createValidLabel() throws Exception {
        utils.regDefaultUser();

        final var response = utils.perform(post(BASE_URL + LABEL_CONTROLLER_PATH)
                        .content(asJson(new LabelDto("New task")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        final Label expectedLabel = labelRepository.findById(actualLabel.getId()).get();

        assertEquals(expectedLabel.getId(), actualLabel.getId());
        assertEquals(expectedLabel.getName(), actualLabel.getName());
        assertThat(actualLabel.getCreatedAt()).isNotNull();
    }

    @Test
    public void createInvalidLabel() throws Exception {
        utils.regDefaultUser();

        final var response = utils.perform(post(BASE_URL + LABEL_CONTROLLER_PATH)
                        .content(asJson(new LabelDto("")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, labelRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");
    }

    @Test
    public void updateLabel() throws Exception {
        utils.regDefaultUser();

        // created label
        final Label oldLabel = utils.createDefaultLabel(TEST_USERNAME);

        // updated label
        final var response = utils.perform(put(BASE_URL + LABEL_CONTROLLER_PATH + ID, oldLabel.getId())
                        .content(asJson(new LabelDto("New label name")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        final Label expectedLabel = labelRepository.findById(oldLabel.getId()).get();

        assertEquals(expectedLabel.getId(), actualLabel.getId());
        assertEquals(expectedLabel.getName(), actualLabel.getName());
        assertThat(actualLabel.getCreatedAt()).isNotNull();
    }

    @Test
    public void updateToInvalidLabel() throws Exception {
        utils.regDefaultUser();

        // created label
        final Label oldLabel = utils.createDefaultLabel(TEST_USERNAME);

        // updated label
        final var response = utils.perform(put(BASE_URL + LABEL_CONTROLLER_PATH + ID, oldLabel.getId())
                        .content(asJson(new LabelDto("")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"name\"");

        // the label was not changed
        final Label label = labelRepository.findById(oldLabel.getId()).get();

        assertEquals(label.getId(), oldLabel.getId());
        assertEquals(label.getName(), oldLabel.getName());
    }

    @Test
    public void updateNonExistentLabel() throws Exception {
        utils.regDefaultUser();

        // update non-existent label
        final var response = utils.perform(put(BASE_URL + LABEL_CONTROLLER_PATH + ID, 1)
                        .content(asJson(new LabelDto("Name")))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Label not found");
    }

    @Test
    public void getAllLabels() throws Exception {
        utils.regDefaultUser();

        // existing label
        final Label existingLabel = utils.createDefaultLabel(TEST_USERNAME);

        // get labels
        final var response = utils.perform(get(BASE_URL + LABEL_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(labels).hasSize(1);

        assertThat(existingLabel.getId()).isEqualTo(labels.get(0).getId());
        assertThat(existingLabel.getCreatedAt()).isEqualTo(labels.get(0).getCreatedAt());
        assertThat(existingLabel.getName()).isEqualTo(labels.get(0).getName());
    }

    @Test
    public void getLabelById() throws Exception {
        utils.regDefaultUser();

        // existing label
        final Label existingLabel = utils.createDefaultLabel(TEST_USERNAME);

        // get label by id
        final var response = utils.perform(
                        get(BASE_URL + LABEL_CONTROLLER_PATH + ID, existingLabel.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(existingLabel.getId()).isEqualTo(actualLabel.getId());
        assertThat(existingLabel.getCreatedAt()).isEqualTo(actualLabel.getCreatedAt());
        assertThat(existingLabel.getName()).isEqualTo(actualLabel.getName());
    }

    @Test
    public void getLabelByNonExistentId() throws Exception {
        utils.regDefaultUser();

        assertEquals(0, labelRepository.count());

        // get label by non-existent id
        final var response = utils.perform(
                        get(BASE_URL + LABEL_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Label not found");

        // no labels
        assertEquals(0, labelRepository.count());
    }

    @Test
    public void deleteLabel() throws Exception {
        utils.regDefaultUser();

        // existing label
        final Label existingLabel = utils.createDefaultLabel(TEST_USERNAME);

        // delete the label
        final var response = utils.perform(delete(BASE_URL + LABEL_CONTROLLER_PATH + ID,
                        existingLabel.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();

        // the label is deleted
        assertEquals(0, labelRepository.count());
    }

    @Test
    public void deleteNonExistentLabel() throws Exception {
        utils.regDefaultUser();

        assertEquals(0, labelRepository.count());

        // delete the label
        final var response = utils.perform(delete(BASE_URL + LABEL_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Label not found");

        // no labels
        assertEquals(0, labelRepository.count());
    }

    @Test
    public void deleteLabelWithExistingTasks() throws Exception {
        utils.regDefaultUser();

        // created task status
        final TaskStatus taskStatus = utils.createDefaultTaskStatus(TEST_USERNAME);

        // existing label
        final Label label = utils.createDefaultLabel(TEST_USERNAME);

        // created task with label
        utils.perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(new TaskDto("New task", null, null, taskStatus.getId(), Set.of(label.getId()))))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        // delete label
        final var response = utils.perform(delete(BASE_URL + LABEL_CONTROLLER_PATH + ID,
                        label.getId()), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Cannot delete the label. Tasks have labels");

        assertEquals(1, labelRepository.count());
    }
}

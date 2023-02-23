package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    public static final String BASE_URL = "/api";

    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "fname",
            "lname",
            "pwd"
    );

    private final TaskStatusDto testTaskStatusDto = new TaskStatusDto("New");

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public void tearDown() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public TaskStatus createDefaultTaskStatus(String username) throws Exception {
        final var response = perform(post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(testTaskStatusDto))
                        .contentType(APPLICATION_JSON), username)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        return fromJson(response.getContentAsString(), new TypeReference<>() {
        });
    }

    public Task createDefaultTask(String username) throws Exception {
        final var taskStatus = createDefaultTaskStatus(username);
        final Long executorId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final var response = perform(post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(new TaskDto("Task name", null, null, taskStatus.getId(), null)))
                .contentType(APPLICATION_JSON), username)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        return fromJson(response.getContentAsString(), new TypeReference<>() {
        });
    }

    public Label createDefaultLabel(String username) throws Exception {
        final var response = perform(post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(new LabelDto("Label name")))
                .contentType(APPLICATION_JSON), username)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        return fromJson(response.getContentAsString(), new TypeReference<>() {
        });
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}

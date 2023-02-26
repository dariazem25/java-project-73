package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.component.JWTHelper;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static hexlet.code.config.SpringConfig.TEST_PROFILE;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class UserControllerTest {

    private static final int MIN_EMAIL_LENGTH = 3;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final String REQUEST_REGISTRATION = "src/test/resources/requests/registration/";
    private static final String REQUEST_MODIFICATION = "src/test/resources/requests/userModification/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @Autowired
    private JWTHelper jwtHelper;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void validRegistration() throws Exception {
        assertEquals(0, userRepository.count());
        final var response = utils.regDefaultUser()
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(1, userRepository.count());
        final User expectedUser = userRepository.findAll().get(0);

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
        assertThat(response.getContentAsString()).contains("email");
        assertThat(response.getContentAsString()).contains("firstName");
        assertThat(response.getContentAsString()).contains("lastName");
        assertThat(response.getContentAsString()).contains("createdAt");
        assertThat(response.getContentAsString()).contains("id");
        assertThat(response.getContentAsString()).doesNotContain("password");
    }

    @Test
    public void registrationFirstNameIsEmpty() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithEmptyFirstName.json").toAbsolutePath()));
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertEquals(0, userRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"firstName\"");
    }

    @Test
    public void registrationLastNameIsEmpty() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithEmptyLastName.json").toAbsolutePath()));
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertEquals(0, userRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"lastName\"");
    }

    @Test
    public void registrationWithValidPassword() throws Exception {
        var body1 = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithMaxLengthOfPassword.json").toAbsolutePath()));
        var body2 = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithMinLengthOfPassword.json").toAbsolutePath()));

        assertEquals(0, userRepository.count());

        // create user with password is equal to max value
        utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        assertEquals(1, userRepository.count());


        // create user with password is equal to min value
        utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        assertEquals(2, userRepository.count());
    }

    @Test
    public void registrationPasswordIsEmpty() throws Exception {
        String body = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithEmptyPassword.json").toAbsolutePath()));
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertEquals(0, userRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"password\"");
    }

    @Test
    public void registrationWithInvalidPassword() throws Exception {
        var body1 = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithPasswordLengthLessThanMin.json").toAbsolutePath()));
        var body2 = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithPasswordLengthGreaterThanMax.json").toAbsolutePath()));
        assertEquals(0, userRepository.count());

        // password size is less than min value
        final var response1 = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body1)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertEquals(0, userRepository.count());
        assertThat(response1.getContentAsString()).contains("\"defaultMessage\":\"size must be between "
                + MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH + "\"");
        assertThat(response1.getContentAsString()).contains("\"defaultMessage\":\"password\"");

        // password size is greater than max value
        final var response2 = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body2)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertEquals(0, userRepository.count());
        assertThat(response2.getContentAsString()).contains("\"defaultMessage\":\"size must be between "
                + MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH + "\"");
        assertThat(response2.getContentAsString()).contains("\"defaultMessage\":\"password\"");
    }

    @Test
    public void registrationWithInvalidEmail() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_REGISTRATION
                + "registrationWithInvalidEmail.json").toAbsolutePath()));
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content(body)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();
        assertEquals(0, userRepository.count());
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must "
                + "be a well-formed email address\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"email\"");
    }

    @Test
    public void getUserByExistingId() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var response = utils.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId()), expectedUser.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    public void getUserWithNotExistingId() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);
        final var response = utils.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, 5), expectedUser.getEmail())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("User not found");
    }

    @Test
    public void getAllUsers() throws Exception {
        utils.regDefaultUser();
        final var response = utils.perform(get(BASE_URL + USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(1);
    }

    @Test
    public void validUpdateUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(asJson(userDto))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User updatedUser = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TEST_USERNAME_2).orElse(null));

        assertEquals(userId, updatedUser.getId());
        assertEquals(userDto.getEmail(), updatedUser.getEmail());
        assertEquals(userDto.getFirstName(), updatedUser.getFirstName());
        assertEquals(userDto.getLastName(), updatedUser.getLastName());

        assertThat(response.getContentAsString()).contains("email");
        assertThat(response.getContentAsString()).contains("firstName");
        assertThat(response.getContentAsString()).contains("lastName");
        assertThat(response.getContentAsString()).contains("createdAt");
        assertThat(response.getContentAsString()).contains("id");
        assertThat(response.getContentAsString()).doesNotContain("password");
    }

    @Test
    public void updateNotExistingUser() throws Exception {
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userBeforeUpdate.getId();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, ++userId)
                        .content(asJson(userDto))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateAnotherUser() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME, "Jane", "Ostin", "123"));
        utils.regUser(new UserDto(TEST_USERNAME_2, "Lili", "Bind", "456"));
        var idUser2 = userRepository.findByEmail(TEST_USERNAME_2).get().getId();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, idUser2)
                        .content(asJson(userDto))
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();

        // User was not changed
        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyFirstName() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithEmptyFirstName.json").toAbsolutePath()));
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"firstName\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyLastName() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithEmptyLastName.json").toAbsolutePath()));
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"lastName\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyPassword() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithEmptyPassword.json").toAbsolutePath()));
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"password\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyEmail() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithEmptyEmail.json").toAbsolutePath()));
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"email\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithInvalidPassword() throws Exception {
        var body1 = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithPasswordLessThanMin.json").toAbsolutePath()));
        var body2 = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithPasswordGreaterThanMax.json").toAbsolutePath()));

        // registered user
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        // password size is less than min value
        final var response1 = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body1)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response1.getContentAsString()).contains("\"defaultMessage\":\"size must be between "
                + MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH + "\"");
        assertThat(response1.getContentAsString()).contains("\"defaultMessage\":\"password\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());

        // password size is greater than max value
        final var response2 = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body2)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response2.getContentAsString()).contains("\"defaultMessage\":\"size must be between "
                + MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH + "\"");
        assertThat(response2.getContentAsString()).contains("\"defaultMessage\":\"password\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithInvalidEmail() throws Exception {
        var body = new String(Files.readAllBytes(Paths.get(REQUEST_MODIFICATION
                + "updateUserWithInvalidEmail.json").toAbsolutePath()));

        // registered user
        utils.regDefaultUser();
        var userBeforeUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content(body)
                        .contentType(APPLICATION_JSON), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate = userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must be "
                + "a well-formed email address\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"email\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void deleteExistingUser() throws Exception {
        utils.regDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final var response = utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();

        assertEquals(0, userRepository.count());
    }

    @Test
    public void deleteNotExistingUser() throws Exception {
        assertEquals(0, userRepository.count());

        final var response = utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, 1), TEST_USERNAME)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    public void deleteAnotherUser() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME, "Kate", "Black", "123"));
        utils.regUser(new UserDto(TEST_USERNAME_2, "Jack", "Black", "128"));
        var idUser2 = userRepository.findByEmail(TEST_USERNAME_2).get().getId();
        assertEquals(2, userRepository.count());

        final var response = utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, idUser2), TEST_USERNAME)
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        // User was not deleted
        assertEquals(2, userRepository.count());
    }

    @Test
    public void validLogin() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME, "Kate", "Black", "123"));
        var loginDto = new LoginDto(TEST_USERNAME, "123");

        final var response = utils.perform(post(BASE_URL + "/login")
                        .content(asJson(loginDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isNotEmpty();
    }

    @Test
    public void loginWithWrongPassword() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME, "Kate", "Black", "123"));
        var loginDto = new LoginDto(TEST_USERNAME, "1234");

        final var response = utils.perform(post(BASE_URL + "/login")
                        .content(asJson(loginDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void loginWithWrongUserName() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME, "Kate", "Black", "123"));
        var loginDto = new LoginDto(TEST_USERNAME_2, "123");

        final var response = utils.perform(post(BASE_URL + "/login")
                        .content(asJson(loginDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void invalidLogin() throws Exception {
        utils.regUser(new UserDto(TEST_USERNAME, "Kate", "Black", "123"));
        var loginDto1 = new LoginDto("", "123");
        var loginDto2 = new LoginDto(TEST_USERNAME, "");

        // login with invalid username
        final var response1 = utils.perform(post(BASE_URL + "/login")
                        .content(asJson(loginDto1))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertThat(response1.getContentAsString()).isEmpty();

        // login with invalid password
        final var response2 = utils.perform(post(BASE_URL + "/login")
                        .content(asJson(loginDto1))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertThat(response2.getContentAsString()).isEmpty();
    }

    @Test
    public void deleteUserWithExistingTasks() throws Exception {
        utils.regDefaultUser();

        // existent task
        utils.createDefaultTask(TEST_USERNAME);

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();
        final var response = utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId), TEST_USERNAME)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("Cannot delete the user. The user has tasks");

        assertEquals(1, userRepository.count());
    }
}

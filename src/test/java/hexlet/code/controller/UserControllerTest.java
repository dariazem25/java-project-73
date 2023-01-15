package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

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
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"\", \"lastName\": \"Bind\", \"password\": "
                                + "\"123\", \"email\": \"j@mail.ru\"}")
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
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"Jackson\", \"lastName\": \"\", \"password\": "
                                + "\"123\", \"email\": \"j@mail.ru\"}")
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
        var password1 = "Ab!@#$%^&*()_-+=~`{}[]V|<>/?K':;.,1234567890123456Ab!@#$%^&*"
                + "()_-+=~`{}[]V|<>/?K':;.,1234567890123456";
        var password2 = "123";

        assertEquals(0, userRepository.count());

        // create user with password is equal to max value
        utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"Jackson\", \"lastName\": \"Bind\", \"password\": "
                                + "\"" + password1 + "\", \"email\": \"j@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        assertEquals(1, userRepository.count());


        // create user with password is equal to min value
        utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"Bind\", \"password\": "
                                + "\"" + password2 + "\", \"email\": \"k@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        assertEquals(2, userRepository.count());
    }

    @Test
    public void registrationPasswordIsEmpty() throws Exception {
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"Jackson\", \"lastName\": \"Bind\", \"password\": "
                                + "\"\", \"email\": \"j@mail.ru\"}")
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
        var invalidPassword1 = "12";
        var invalidPassword2 = "Ab!@#$%^&*()_-+=~`{}[]V|<>/?K':;.,1234567890123456Ab!@"
                + "#$%^&*()_-+=~`{}[]V|<>/?K':;.,1234567890123456!";
        assertEquals(0, userRepository.count());

        // password size is less than min value
        final var response1 = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"Jackson\", \"lastName\": \"Bind\", \"password\": "
                                + "\"" + invalidPassword1 + "\", \"email\": \"j@mail.ru\"}")
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
                        .content("{\"firstName\": \"Jackson\", \"lastName\": \"Bind\", \"password\": \""
                                + invalidPassword2 + "\", \"email\": \"j@mail.ru\"}")
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
        assertEquals(0, userRepository.count());
        final var response = utils.perform(post(BASE_URL + USER_CONTROLLER_PATH)
                        .content("{\"firstName\": \"Jackson\", \"lastName\": \"Bind\", \"password\": "
                                + "\"123\", \"email\": \"j@\"}")
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
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId()))
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
        assertEquals(0, userRepository.count());
        final var response = utils.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, 1))
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
                        .contentType(APPLICATION_JSON))
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
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, ++userId)
                        .content(asJson(userDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("User not found");
        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyFirstName() throws Exception {
        utils.regDefaultUser();
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"\", \"lastName\": \"Bind\", \"password\": "
                                + "\"123\", \"email\": \"j@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"firstName\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyLastName() throws Exception {
        utils.regDefaultUser();
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"\", \"password\": "
                                + "\"123\", \"email\": \"j@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"lastName\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyPassword() throws Exception {
        utils.regDefaultUser();
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"Bind\", \"password\": "
                                + "\"\", \"email\": \"j@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"password\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithEmptyEmail() throws Exception {
        utils.regDefaultUser();
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"Bind\", \"password\": "
                                + "\"123\", \"email\": \"\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"must not be blank\"");
        assertThat(response.getContentAsString()).contains("\"defaultMessage\":\"email\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());
    }

    @Test
    public void updateUserWithInvalidPassword() throws Exception {
        var invalidPassword1 = "12";
        var invalidPassword2 = "Ab!@#$%^&*()_-+=~`{}[]V|<>/?K':;.,1234567"
                + "890123456Ab!@#$%^&*()_-+=~`{}[]V|<>/?K':;.,1234567890123456!";

        // registered user
        utils.regDefaultUser();
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        // password size is less than min value
        final var response1 = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"Bind\", \"password\": "
                                + "\"" + invalidPassword1 + "\", \"email\": \"j@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        assertThat(response1.getContentAsString()).contains("\"defaultMessage\":\"size must be between "
                + MIN_EMAIL_LENGTH + " and " + MAX_EMAIL_LENGTH + "\"");
        assertThat(response1.getContentAsString()).contains("\"defaultMessage\":\"password\"");

        assertEquals(userBeforeUpdate.getId(), userAfterUpdate.getId());
        assertEquals(userBeforeUpdate.getEmail(), userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getFirstName(), userAfterUpdate.getFirstName());
        assertEquals(userBeforeUpdate.getLastName(), userAfterUpdate.getLastName());

        // password size is greater than max value
        final var response2 = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"Bind\", \"password\": "
                                + "\"" + invalidPassword2 + "\", \"email\": \"j@mail.ru\"}")
                        .contentType(APPLICATION_JSON))
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
        // registered user
        utils.regDefaultUser();
        var userBeforeUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
        var userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var response = utils.perform(put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                        .content("{\"firstName\": \"Kate\", \"lastName\": \"Bind\", \"password\": "
                                + "\"123\", \"email\": \"j@\"}")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse();

        var userAfterUpdate =  userRepository.findByEmail(TEST_USERNAME).get();
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
        final var response = utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).isEmpty();

        assertEquals(0, userRepository.count());
    }

    @Test
    public void deleteNotExistingUser() throws Exception {
        assertEquals(0, userRepository.count());

        final var response = utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, 1))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains("User not found");
    }
}

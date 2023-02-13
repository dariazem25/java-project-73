package hexlet.code.service;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;

public interface UserService {

    User createNewUser(UserDto userDto);

    User updateUser(Long id, UserDto userDto);

    User getUser(Long id);

    void deleteUser(Long id);

    String getCurrentUserName();

    User getCurrentUser();
}

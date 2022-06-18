package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserDeleteException;
import ru.yandex.practicum.filmorate.exceptions.UserGetException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> usersList = new HashMap<>();

    @Override
    public List<User> findAll() {
        log.info("Get User list size: {}", usersList.size());
        return new ArrayList<>(usersList.values());
    }

    @Override
    public User create(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        log.info("Attempt Create User");
        if (dtoUser == null || dtoUser.getEmail() == null) {
            log.error("Create User: Invalid Email.");
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);

        if (usersList.containsValue(user)) {
            log.error("Create User: User already exists.");
            throw new UserAlreadyExistException("Error: User is already exists.");
        } else {
            long id = usersList.size() + 1;
            user.setId(id);
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            usersList.put(id, user);
        }
        log.info("Create User: {}", user);

        return user;
    }

    @Override
    public User update(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        log.info("Attempt Update User");
        if (dtoUser == null || dtoUser.getEmail() == null) {
            log.error("Update User: Invalid Email");
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);
        long id = user.getId();
        if (!usersList.containsKey(id)) {
            log.error("Update User: User is unknown.");
            throw new UserAlreadyExistException("Error: User is unknown.");
        }
        usersList.put(id, user);
        log.info("Update User: {}", user);

        return user;
    }

    @Override
    public void remove(DtoUser dtoUser) throws UserDeleteException {
        log.info("Attempt Delete User");
        if (dtoUser == null) {
            log.error("Update User: Invalid User");
            throw new UserDeleteException("Error: User is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);
        long id = user.getId();
        if (!usersList.containsKey(id)) {
            log.error("Delete User: User is not found.");
            throw new UserDeleteException("Error: User is not found.");
        }
        usersList.remove(id);
        log.info("Delete User: {}", user);
    }

    @Override
    public User getUserById(Long userId) throws UserGetException {
        if (userId == null || userId < 0) {
            log.error("Get User: Invalid ID");
            throw new UserGetException("Error: Invalid ID.");
        }
        if (!usersList.containsKey(userId)) {
            log.error("Get User: User ID is not found.");
            throw new UserGetException("Error: User ID is not found.");
        }
        return usersList.get(userId);
    }
}

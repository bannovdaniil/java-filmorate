package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> usersList = new HashMap<>();

    @GetMapping
    public List<User> getUsersPage() {
        log.info("Get User list size: {}", usersList.size());
        return new ArrayList<>(usersList.values());
    }

    @PostMapping
    public User create(@RequestBody @Valid DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        log.info("Attempt Create User");
        if (dtoUser == null || dtoUser.getEmail() == null) {
            log.error("Create User: Invalid Email.");
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);

        if (usersList.containsValue(user)) {
            log.error("Create User: User already exists.");
            throw new UserAlreadyExistException("Error: User is exists");
        } else {
            int id = usersList.size() + 1;
            user.setId(id);
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            usersList.put(id, user);
        }
        log.info("Create User: {}", user);

        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid DtoUser dtoUser) throws InvalidEmailException {
        log.info("Attempt Update User");
        if (dtoUser == null || dtoUser.getEmail() == null) {
            log.error("Update User: Invalid Email");
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);
        int id = user.getId();
        if (!usersList.containsKey(user.getId())) {
            log.error("Update User: User is unknown");
            throw new UserAlreadyExistException("Error: User is unknown.");
        }
        usersList.put(id, user);
        log.info("Update User: {}", user);

        return user;
    }
}

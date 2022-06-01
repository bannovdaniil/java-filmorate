package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> usersList = new HashMap<>();

    @GetMapping
    public List<User> getUsersPage() {
        return new ArrayList<>(usersList.values());
    }

    @PostMapping
    public User create(@RequestBody DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        if (dtoUser == null || dtoUser.getEmail() == null) {
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);

        if (usersList.containsValue(user)) {
            throw new UserAlreadyExistException("Error: User is exists");
        } else {
            int id = usersList.size() + 1;
            user.setId(id);
            usersList.put(id, user);
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody DtoUser dtoUser) throws InvalidEmailException {
        if (dtoUser == null || dtoUser.getEmail() == null) {
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        User user = DtoMapper.dtoToUser(dtoUser);
        int id = usersList.size();
        if (!usersList.containsValue(user)) {
            user.setId(id);
        } else {
            id = user.getId();
        }
        usersList.put(id, user);
        return user;
    }
}

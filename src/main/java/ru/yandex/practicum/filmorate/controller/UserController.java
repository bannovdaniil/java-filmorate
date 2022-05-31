package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final List<User> usersList = new ArrayList<>();

    @GetMapping
    public List<User> getUsersPage() {
        return usersList;
    }

    @PostMapping
    public User create(@RequestBody User user) throws InvalidEmailException, UserAlreadyExistException {
        if (user == null || user.getEmail() == null) {
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        if (usersList.contains(user)) {
            throw new UserAlreadyExistException("Error: User is exists");
        } else {
            usersList.add(user);
        }
        return usersList.get(usersList.indexOf(user));
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidEmailException {
        if (user == null || user.getEmail() == null) {
            throw new InvalidEmailException("Error: E-mail is null.");
        }
        if (usersList.contains(user)) {
            usersList.set(usersList.indexOf(user), user);
        } else {
            usersList.add(user);
        }
        return usersList.get(usersList.indexOf(user));
    }
}

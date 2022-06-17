package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserDeleteException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> getUsersPage() {
        return userStorage.findAll();
    }

    @PostMapping
    public User create(@RequestBody @Valid DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return userStorage.create(dtoUser);
    }

    @PutMapping
    public User update(@RequestBody @Valid DtoUser dtoUser) throws UserAlreadyExistException, InvalidEmailException {
        return userStorage.update(dtoUser);
    }

    @DeleteMapping
    public void delete(@RequestBody @Valid DtoUser dtoUser) throws UserDeleteException {
        userStorage.delete(dtoUser);
    }
}

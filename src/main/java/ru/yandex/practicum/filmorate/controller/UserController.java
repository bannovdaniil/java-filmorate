package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsersList() {
        return userService.getUserStorage().findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long userId) throws UserNotFoundException {
        return userService.getUserStorage().getUserById(userId);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return userService.getUserStorage().create(dtoUser);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid DtoUser dtoUser) throws UserAlreadyExistException, InvalidEmailException {
        return userService.getUserStorage().update(dtoUser);
    }

    @DeleteMapping
    public void removeUser(@RequestBody @Valid DtoUser dtoUser) throws UserRemoveException {
        userService.getUserStorage().remove(dtoUser);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId) throws UserNotFoundException {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId) throws UserNotFoundException, UserRemoveException {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") Long userId) throws UserNotFoundException {
        return userService.getFriendList(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCrossFriend(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId) throws UserNotFoundException {
        return userService.getCrossFriendList(userId, otherId);
    }


}



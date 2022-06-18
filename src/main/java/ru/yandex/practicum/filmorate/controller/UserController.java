package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserDeleteException;
import ru.yandex.practicum.filmorate.exceptions.UserGetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsersPage() {
        return userStorage.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return userStorage.create(dtoUser);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid DtoUser dtoUser) throws UserAlreadyExistException, InvalidEmailException {
        return userStorage.update(dtoUser);
    }

    @DeleteMapping
    public void removeUser(@RequestBody @Valid DtoUser dtoUser) throws UserDeleteException {
        userStorage.remove(dtoUser);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId) throws UserGetException {
        User user = userStorage.getUserById(userId);
        if (userStorage.getUserById(friendId) != null) {
            userService.addFriend(user, friendId);
        }
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable("id") Long userId,
            @PathVariable("friendId") Long friendId) throws UserGetException, UserDeleteException {
        User user = userStorage.getUserById(userId);
        if (userStorage.getUserById(friendId) != null) {
            if (!userService.removeFriend(user, friendId)) {
                throw new UserDeleteException("Can't delete friend.");
            }
        }
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") Long userId) throws UserGetException {
        User user = userStorage.getUserById(userId);
        List<User> resultFriendsList = new ArrayList<>();
        Set<Long> friendsIds = userService.getFriendList(user);

        for (Long friendId : friendsIds) {
            resultFriendsList.add(userStorage.getUserById(friendId));
        }

        return resultFriendsList;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCrossFriend(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId) throws UserGetException {
        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);

        Set<Long> friendsIdsUnion = new HashSet<>(userService.getFriendList(user));
        friendsIdsUnion.retainAll(userService.getFriendList(other));

        List<User> resultFriendsList = new ArrayList<>();

        for (Long friendId : friendsIdsUnion) {
            resultFriendsList.add(userStorage.getUserById(friendId));
        }

        return resultFriendsList;
    }

}



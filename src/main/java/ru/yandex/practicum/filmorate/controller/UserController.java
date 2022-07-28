package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaRatingNotFound;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EventService eventService;
    private final FilmService filmService;

    @GetMapping
    public List<User> getUsersList() {
        return userService.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid UserDto userDto) throws UserNotFoundException {
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") Long userId) throws UserRemoveException {
        userService.removeUserById(userId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long userId) throws UserNotFoundException {
        return userService.getUserById(userId);
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

    @GetMapping("{id}/feed")
    public List<Event> getUserFeed(@PathVariable("id") Long userId) {
        return eventService.findAllEventsByUserId(userId);
    }

    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") int userId) throws MpaRatingNotFound, FilmNotFoundException {
        return filmService.getRecommendations(userId);
    }
}



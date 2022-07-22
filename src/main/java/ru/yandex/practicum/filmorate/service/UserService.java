package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final EventService eventService;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(DtoUser dtoUser) {
        return userStorage.create(dtoUser);
    }

    public User update(DtoUser dtoUser) throws UserNotFoundException {
        return userStorage.update(dtoUser);
    }

    public void remove(DtoUser dtoUser) throws UserRemoveException {
        userStorage.remove(dtoUser);
    }

    public User getUserById(Long userId) throws UserNotFoundException {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Long userId, Long friendId) throws UserNotFoundException {
        userStorage.addFriend(userId, friendId);
        eventService.addEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void removeFriend(Long userId, Long friendId) throws UserNotFoundException, UserRemoveException {
        userStorage.removeFriend(userId, friendId);
        eventService.addEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public List<User> getFriendList(Long userId) throws UserNotFoundException {
        return userStorage.getFriendList(userId);
    }

    public List<User> getCrossFriendList(Long userId, Long otherId) throws UserNotFoundException {
        return userStorage.getCrossFriendList(userId, otherId);
    }
}

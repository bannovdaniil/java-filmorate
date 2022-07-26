package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(UserDto userDto);

    User update(UserDto userDto) throws UserNotFoundException;

    boolean isUserExist(long userId);

    void remove(UserDto userDto) throws UserRemoveException;

    User getUserById(Long userId) throws UserNotFoundException;


    void addFriend(Long userId, Long friendId) throws UserNotFoundException;

    void removeFriend(Long userId, Long friendId) throws UserNotFoundException, UserRemoveException;

    List<User> getFriendList(Long userId) throws UserNotFoundException;

    List<User> getCrossFriendList(Long userId, Long otherId) throws UserNotFoundException;

    void removeUserById(Long userId) throws UserRemoveException;
}

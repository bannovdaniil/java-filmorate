package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(DtoUser dtoUser);

    User update(DtoUser dtoUser) throws UserNotFoundException;

    boolean isUserExist(long userId);

    void remove(DtoUser dtoUser) throws UserRemoveException;

    User getUserById(Long userId) throws UserNotFoundException;


    void addFriend(Long userId, Long friendId) throws UserNotFoundException;

    void removeFriend(Long userId, Long friendId) throws UserNotFoundException, UserRemoveException;

    List<User> getFriendList(Long userId) throws UserNotFoundException;

    List<User> getCrossFriendList(Long userId, Long otherId) throws UserNotFoundException;
}

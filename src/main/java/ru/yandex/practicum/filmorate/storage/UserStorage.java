package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.exceptions.UserGetException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException;

    User update(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException;

    void remove(DtoUser dtoUser) throws UserRemoveException;

    User getUserById(Long userId) throws UserGetException;
}

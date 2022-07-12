package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRemoveException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public class UserDbStorage implements UserStorage{
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User create(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return null;
    }

    @Override
    public User update(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException {
        return null;
    }

    @Override
    public void remove(DtoUser dtoUser) throws UserRemoveException {

    }

    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        return null;
    }
}

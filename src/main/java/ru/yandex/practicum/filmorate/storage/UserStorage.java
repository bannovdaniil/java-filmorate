package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.dto.DtoUser;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserDeleteException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException;

    User update(DtoUser dtoUser) throws InvalidEmailException, UserAlreadyExistException;

    void delete(DtoUser dtoUser) throws UserDeleteException;
}

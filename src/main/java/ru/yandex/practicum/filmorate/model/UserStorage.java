package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User create(User user);

    User update(User user);

    boolean delete(User user);
}

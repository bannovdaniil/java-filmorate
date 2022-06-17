package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    boolean delete(Film film);
}

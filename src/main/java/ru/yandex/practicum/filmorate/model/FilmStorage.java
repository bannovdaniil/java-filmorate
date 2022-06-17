package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmDeleteException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();
    Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException;

    Film update(DtoFilm dtoFilm) throws InvalidFilmException;

    void delete(DtoFilm dtoFilm) throws InvalidFilmDeleteException;
}

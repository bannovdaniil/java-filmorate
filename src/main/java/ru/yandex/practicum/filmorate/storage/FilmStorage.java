package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException;

    Film update(DtoFilm dtoFilm) throws InvalidFilmException;

    void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException;

    Film getFilmById(Long filmId) throws FilmNotFoundException;
}

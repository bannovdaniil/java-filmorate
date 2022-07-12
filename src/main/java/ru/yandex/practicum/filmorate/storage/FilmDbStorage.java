package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmRemoveException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public class FilmDbStorage implements FilmStorage {
    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException {
        return null;
    }

    @Override
    public Film update(DtoFilm dtoFilm) throws InvalidFilmException {
        return null;
    }

    @Override
    public void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException {

    }

    @Override
    public Film getFilmById(Long filmId) throws FilmNotFoundException {
        return null;
    }
}

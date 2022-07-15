package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll() throws MpaRatingNotFound;

    Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException, MpaRatingNotFound, GenreNotFound, MpaRatingNotValid;

    Film update(DtoFilm dtoFilm) throws InvalidFilmException, FilmNotFoundException, MpaRatingNotFound, MpaRatingNotValid;

    void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException;

    Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound;

    List<Film> getFilmTop(Long count) throws MpaRatingNotFound;

    boolean isFilmExist(long filmId);

    void addLike(long filmId);

    void removeLike(long filmId);
}

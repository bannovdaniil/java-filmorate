package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll() throws MpaRatingNotFound;

    Film create(DtoFilm dtoFilm) throws MpaRatingNotFound, GenreNotFound, MpaRatingNotValid;

    Film update(DtoFilm dtoFilm) throws FilmNotFoundException, MpaRatingNotFound, MpaRatingNotValid, GenreNotFound;

    void remove(DtoFilm dtoFilm) throws InvalidFilmRemoveException;

    Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound;

    List<Film> getFilmTop(Long count, Integer genreId, Integer year) throws MpaRatingNotFound;

    boolean isFilmExist(long filmId);

    void addLike(long filmId);

    void removeLike(long filmId);
}

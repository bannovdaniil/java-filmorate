package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll() throws MpaRatingNotFound;

    Film create(FilmDto filmDto) throws MpaRatingNotFound, GenreNotFound, DirectorNotFoundException;

    Film update(FilmDto filmDto) throws FilmNotFoundException, MpaRatingNotFound, GenreNotFound, DirectorNotFoundException;

    void remove(FilmDto filmDto) throws InvalidFilmRemoveException;

    Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound;

    List<Film> getFilmTop(Long count, Integer genreId, Integer year) throws MpaRatingNotFound;

    boolean isFilmExist(long filmId);

    void addLike(long filmId);

    void removeLike(long filmId);

    List<Film> getRecommendations(int userId) throws MpaRatingNotFound;

    List<Film> getFilmsByDirectorOrderByDate(int id) throws MpaRatingNotFound, DirectorNotFoundException;

    List<Film> getFilmsByDirectorOrderByLikes(int id) throws MpaRatingNotFound, DirectorNotFoundException;

    void removeFilmById(Long filmId) throws FilmNotFoundException;

    List<Film> getCommonFilms(long userId, long friendId) throws MpaRatingNotFound, UserNotFoundException;

    List<Film> searchFilms(String query, List<String> searchByParams) throws MpaRatingNotFound;
}

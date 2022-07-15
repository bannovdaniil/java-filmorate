package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class FilmService {
    private FilmStorage filmStorage;
    private LikeStorage likeStorage;

    @Autowired
    public void setFilmStorage(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Autowired
    public void setLikeStorage(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    public List<Film> findAll() throws MpaRatingNotFound {
        return filmStorage.findAll();
    }

    public Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException, MpaRatingNotFound, GenreNotFound, MpaRatingNotValid {
        return filmStorage.create(dtoFilm);
    }

    public Film update(DtoFilm dtoFilm) throws InvalidFilmException, FilmNotFoundException, MpaRatingNotFound, MpaRatingNotValid, GenreNotFound {
        return filmStorage.update(dtoFilm);
    }

    public void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmStorage.delete(dtoFilm);

    }

    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        return filmStorage.getFilmById(filmId);
    }


    public void addLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getFilmTop(Long count) throws MpaRatingNotFound {
        return filmStorage.getFilmTop(count);
    }

}

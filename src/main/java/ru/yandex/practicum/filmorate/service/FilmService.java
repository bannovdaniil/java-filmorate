package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    public List<Film> findAll() throws MpaRatingNotFound {
        return filmStorage.findAll();
    }

    public Film create(DtoFilm dtoFilm) throws MpaRatingNotFound, GenreNotFound, MpaRatingNotValid {
        return filmStorage.create(dtoFilm);
    }

    public Film update(DtoFilm dtoFilm) throws FilmNotFoundException, MpaRatingNotFound, MpaRatingNotValid, GenreNotFound {
        return filmStorage.update(dtoFilm);
    }

    public void remove(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmStorage.remove(dtoFilm);

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

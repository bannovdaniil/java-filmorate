package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public List<Film> findAll() throws MpaRatingNotFound {
        return filmStorage.findAll();
    }

    public Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException, MpaRatingNotFound, GenreNotFound {
        return filmStorage.create(dtoFilm);
    }

    public Film update(DtoFilm dtoFilm) throws InvalidFilmException, FilmNotFoundException, MpaRatingNotFound {
        return filmStorage.update(dtoFilm);
    }

    public void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmStorage.delete(dtoFilm);

    }

    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        return filmStorage.getFilmById(filmId);
    }


    public void addLike(Long filmId, Long userId) throws FilmNotFoundException, MpaRatingNotFound {
        if (likeStorage.addLike(filmId, userId)) {
            Film film = filmStorage.getFilmById(filmId);
            film.addLike();
            // TODO Update DB film record
        }
    }

    public void removeLike(Long filmId, Long userId) throws FilmNotFoundException, FilmRemoveLikeException, MpaRatingNotFound {
        if (likeStorage.removeLike(filmId, userId)) {
            Film film = filmStorage.getFilmById(filmId);
            film.removeLike();
            // TODO Update DB film record
        } else {
            throw new FilmRemoveLikeException("Can't delete like.");
        }
    }

    public List<Film> getFilmTop(Long count) throws MpaRatingNotFound {
       return filmStorage.getFilmTop(count);
    }

}

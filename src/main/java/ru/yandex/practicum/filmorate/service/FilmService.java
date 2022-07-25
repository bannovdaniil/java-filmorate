package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final EventService eventService;

    private final DirectorStorage directorStorage;

    public List<Film> findAll() throws MpaRatingNotFound {
        return filmStorage.findAll();
    }

    public Film create(DtoFilm dtoFilm) throws MpaRatingNotFound, GenreNotFound, MpaRatingNotValid, DirectorNotFoundException {
        return filmStorage.create(dtoFilm);
    }

    public Film update(DtoFilm dtoFilm) throws FilmNotFoundException, MpaRatingNotFound, MpaRatingNotValid, GenreNotFound, DirectorNotFoundException {
        return filmStorage.update(dtoFilm);
    }

    public void remove(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmStorage.remove(dtoFilm);

    }

    public Film getFilmById(Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmLikeStorage.addLike(filmId, userId);
        eventService.addEvent(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void removeLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmLikeStorage.removeLike(filmId, userId);
        eventService.addEvent(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public List<Film> getFilmTop(Long count, Integer genreId, Integer year) throws MpaRatingNotFound {
        return filmStorage.getFilmTop(count, genreId, year);
    }

    public List<Film> getFilmsByDirectorsSorted(int id, String sortBy) throws FilmNotFoundException, MpaRatingNotFound,
            RequestParamNotValid, DirectorNotFoundException {
        if (sortBy.equals("year")){
            return filmStorage.getFilmsByDirectorOrderByDate(id);
        } else if (sortBy.equals("likes")) {
            return filmStorage.getFilmsByDirectorOrderByLikes(id);
        } else {
            throw new RequestParamNotValid("Параметр запроса неправильный: " + sortBy);
        }
    }

    public List<Film> getCommonFilms(long userId, long friendId) throws UserNotFoundException, MpaRatingNotFound {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}

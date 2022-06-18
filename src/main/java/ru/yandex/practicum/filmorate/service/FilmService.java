package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmGetException;
import ru.yandex.practicum.filmorate.exceptions.FilmRemoveLikeException;
import ru.yandex.practicum.filmorate.exceptions.UserGetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) throws UserGetException, FilmGetException {
        if (userStorage.getUserById(userId) != null) {
            Film film = filmStorage.getFilmById(filmId);
            film.addLike(userId);
        }
    }

    public void removeLike(Long filmId, Long userId) throws UserGetException, FilmGetException, FilmRemoveLikeException {
        if (userStorage.getUserById(userId) != null) {
            Film film = filmStorage.getFilmById(filmId);
            if (film.removeLike(userId)) {
                throw new FilmRemoveLikeException("Can't delete like.");
            }
        }
    }


    public List<Film> getFilmTop(Long count) {
        Map<Integer, Film> filmRates = new TreeMap<>(Collections.reverseOrder());
        List<Film> filmList = filmStorage.findAll();

        for (Film film : filmList) {
            filmRates.put(film.getRate(), film);
        }

        return filmRates.values().stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}

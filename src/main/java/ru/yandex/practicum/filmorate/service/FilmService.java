package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmRemoveLikeException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
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

    public void addLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        if (userStorage.getUserById(userId) != null) {
            Film film = filmStorage.getFilmById(filmId);
            film.addLike(userId);
        }
    }

    public void removeLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException, FilmRemoveLikeException {
        if (userStorage.getUserById(userId) != null) {
            Film film = filmStorage.getFilmById(filmId);
            if (film.removeLike(userId)) {
                throw new FilmRemoveLikeException("Can't delete like.");
            }
        }
    }


    public List<Film> getFilmTop(Long count) {
        List<Film> filmList = filmStorage.findAll();

        return filmList.stream()
                .sorted((film1, film2) -> film2.getRate().compareTo(film1.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }
}

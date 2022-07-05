package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException {
        return filmStorage.create(dtoFilm);
    }

    public Film update(DtoFilm dtoFilm) throws InvalidFilmException {
        return filmStorage.update(dtoFilm);
    }

    public void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmStorage.delete(dtoFilm);

    }

    public Film getFilmById(Long filmId) throws FilmNotFoundException {
        return filmStorage.getFilmById(filmId);
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

}

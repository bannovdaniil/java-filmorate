package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmRemoveException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> filmsList = new HashMap<>();

    @Override
    public List<Film> findAll() {
        log.info("Get Film list size: {}", filmsList.size());
        return new ArrayList<>(filmsList.values());
    }

    @Override
    public Film create(DtoFilm dtoFilm) throws UserAlreadyExistException, InvalidFilmException {
        log.info("Attempt Create Film record");
        if (dtoFilm == null || dtoFilm.getName() == null) {
            log.error("Create Film: Invalid film name.");
            throw new InvalidFilmException("Error: Film name is null.");
        }
        Film film = DtoMapper.dtoToFilm(dtoFilm);
        if (filmsList.containsValue(film)) {
            log.error("Create Film: Film already exists.");
            throw new UserAlreadyExistException("Error: Film already exists.");
        } else {
            long id = filmsList.size() + 1;
            film.setId(id);
            filmsList.put(id, film);
        }
        log.info("Create Film: {}", film);

        return film;
    }

    @Override
    public Film update(DtoFilm dtoFilm) throws InvalidFilmException {
        log.info("Attempt Update Film record");
        if (dtoFilm == null || dtoFilm.getName() == null) {
            log.error("Update Film: Film name is null.");
            throw new InvalidFilmException("Error: Film name is null.");
        }

        Film film = DtoMapper.dtoToFilm(dtoFilm);
        long id = film.getId();
        if (!filmsList.containsKey(id)) {
            log.error("Update Film: Film is unknown.");
            throw new InvalidFilmException("Error: Film is unknown.");
        }
        filmsList.put(id, film);
        log.info("Update Film: {}", film);

        return film;
    }

    @Override
    public void delete(DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        log.info("Attempt Delete Film record");
        if (dtoFilm == null) {
            log.error("Delete Film: Film is null.");
            throw new InvalidFilmRemoveException("Error: Film name is null.");
        }

        Film film = DtoMapper.dtoToFilm(dtoFilm);
        long id = film.getId();
        if (!filmsList.containsKey(id)) {
            log.error("Delete Film: Film is not found.");
            throw new InvalidFilmRemoveException("Error: Film is not found.");
        }
        filmsList.remove(id);
        log.info("Delete Film id: {}", id);
    }

    @Override
    public Film getFilmById(Long filmId) throws FilmNotFoundException {
        if (filmId == null || filmId < 0) {
            log.error("Get Film: Invalid ID");
            throw new FilmNotFoundException("Error: Invalid ID.");
        }
        if (!filmsList.containsKey(filmId)) {
            log.error("Get User: User ID is not found.");
            throw new FilmNotFoundException("Error: Film ID is not found.");
        }
        return filmsList.get(filmId);
    }
}

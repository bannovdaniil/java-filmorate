package ru.yandex.practicum.filmorate.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmsList = new HashMap<>();

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
            int id = filmsList.size() + 1;
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
        int id = film.getId();
        if (!filmsList.containsKey(id)) {
            log.error("Update Film: Film is unknown.");
            throw new InvalidFilmException("Error: Film is unknown.");
        }
        filmsList.put(id, film);
        log.info("Update Film: {}", film);

        return film;
    }

    @Override
    public void delete(DtoFilm dtoFilm) throws InvalidFilmException {
        log.info("Attempt Delete Film record");
        if (dtoFilm == null) {
            log.error("Delete Film: Film is null.");
            throw new InvalidFilmException("Error: Film name is null.");
        }

        Film film = DtoMapper.dtoToFilm(dtoFilm);
        int id = film.getId();
        if (!filmsList.containsKey(id)) {
            log.error("Delete Film: Film is not found.");
            throw new InvalidFilmException("Error: Film is not found.");
        }
        filmsList.remove(id);
        log.info("Delete Film id: {}", id);
    }
}

package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.mapper.DtoMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> filmsList = new HashMap<>();

    @GetMapping
    public List<Film> getUsersPage() {
        return new ArrayList<>(filmsList.values());
    }

    @PostMapping
    public Film create(@RequestBody DtoFilm dtoFilm) {
        if (dtoFilm == null || dtoFilm.getName() == null) {
            throw new InvalidFilmException("Error: Film name is null.");
        }
        Film film = DtoMapper.dtoToFilm(dtoFilm);
        if (filmsList.containsValue(film)) {
            throw new UserAlreadyExistException("Error: Film is exists");
        } else {
            int id = filmsList.size() + 1;
            film.setId(id);
            filmsList.put(id, film);
        }
        return film;
    }

    @PutMapping
    public Film update(@RequestBody DtoFilm dtoFilm) throws InvalidFilmException {
        if (dtoFilm == null || dtoFilm.getName() == null) {
            throw new InvalidFilmException("Error: Film name is null.");
        }

        Film film = DtoMapper.dtoToFilm(dtoFilm);
        int id = filmsList.size();
        if (!filmsList.containsValue(film)) {
            film.setId(id);
        } else {
            id = film.getId();
        }
        filmsList.put(id, film);
        return film;
    }
}

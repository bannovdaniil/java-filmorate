package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final List<Film> filmsList = new ArrayList<>();

    @GetMapping
    public List<Film> getUsersPage() {
        return filmsList;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film == null || film.getName() == null) {
            throw new InvalidFilmException("Error: Film name is null.");
        }
        if (filmsList.contains(film)) {
            throw new UserAlreadyExistException("Error: Film is exists");
        } else {
            filmsList.add(film);
        }
        return filmsList.get(filmsList.indexOf(film));
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidFilmException {
        if (film == null || film.getName() == null) {
            throw new InvalidFilmException("Error: Film name is null.");
        }
        if (filmsList.contains(film)) {
            filmsList.set(filmsList.indexOf(film), film);
        } else {
            filmsList.add(film);
        }
        return filmsList.get(filmsList.indexOf(film));
    }
}

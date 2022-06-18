package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmDeleteException;
import ru.yandex.practicum.filmorate.exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getUsersPage() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException {
        return filmStorage.create(dtoFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmException {
        return filmStorage.update(dtoFilm);
    }

    @DeleteMapping
    public String delete(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmDeleteException {
        filmStorage.delete(dtoFilm);
        return "delete - ok";
    }

}

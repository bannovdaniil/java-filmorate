package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilmList() {
        return filmService.getFilmStorage().findAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Long filmId) throws FilmNotFoundException {
        return filmService.getFilmStorage().getFilmById(filmId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmException, UserAlreadyExistException {
        return filmService.getFilmStorage().create(dtoFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmException {
        return filmService.getFilmStorage().update(dtoFilm);
    }

    @DeleteMapping
    public String delete(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmService.getFilmStorage().delete(dtoFilm);
        return "delete - ok";
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId) throws UserNotFoundException, FilmNotFoundException {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId) throws UserNotFoundException, FilmNotFoundException, FilmRemoveLikeException {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmTop(@RequestParam(defaultValue = "10", required = false) Long count) {
        return filmService.getFilmTop(count);
    }
}

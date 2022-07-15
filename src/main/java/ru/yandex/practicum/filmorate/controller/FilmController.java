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
    public List<Film> getFilmList() throws MpaRatingNotFound {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmException
            , UserAlreadyExistException
            , MpaRatingNotFound
            , GenreNotFound
            , MpaRatingNotValid {
        return filmService.create(dtoFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmException
            , FilmNotFoundException
            , MpaRatingNotFound
            , MpaRatingNotValid
            , GenreNotFound {
        return filmService.update(dtoFilm);
    }

    @DeleteMapping
    public String delete(@Valid @RequestBody DtoFilm dtoFilm) throws InvalidFilmRemoveException {
        filmService.delete(dtoFilm);
        return "delete - ok";
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmTop(@RequestParam(defaultValue = "10", required = false) Long count) throws MpaRatingNotFound {
        return filmService.getFilmTop(count);
    }
}

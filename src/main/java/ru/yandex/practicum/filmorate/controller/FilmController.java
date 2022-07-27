package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
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
    public Film create(@Valid @RequestBody FilmDto filmDto) throws MpaRatingNotFound
            , GenreNotFound
            , MpaRatingNotValid, DirectorNotFoundException {
        return filmService.create(filmDto);
    }

    @PutMapping
    public Film update(@Valid @RequestBody FilmDto filmDto) throws FilmNotFoundException
            , MpaRatingNotFound
            , MpaRatingNotValid
            , GenreNotFound, DirectorNotFoundException {
        return filmService.update(filmDto);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable("id") Long filmId) throws FilmNotFoundException {
        filmService.removeFilmById(filmId);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Long filmId) throws FilmNotFoundException, MpaRatingNotFound {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId,
            @RequestParam(required = false, defaultValue = "10") Integer rate
            ) throws FilmNotFoundException, UserNotFoundException, RequestParamNotValid {
        filmService.addLike(filmId, userId, rate);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmTop(
            @RequestParam(defaultValue = "10", required = false) Long count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) throws MpaRatingNotFound {
        return filmService.getFilmTop(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable("directorId") Integer id, @RequestParam String sortBy)
            throws MpaRatingNotFound, RequestParamNotValid, DirectorNotFoundException {
        return filmService.getFilmsByDirectorsSorted(id, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId
    ) throws UserNotFoundException, MpaRatingNotFound {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam(name = "by", defaultValue = "title") List<String> searchByParams) throws MpaRatingNotFound {
        return filmService.searchFilms(query, searchByParams);
    }
}

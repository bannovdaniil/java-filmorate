package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DtoFilm;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmDtoMapper {
    public static Film dtoToFilm(DtoFilm dtoFilm) {
        Film film = new Film();
        film.setId(dtoFilm.getId());
        film.setName(dtoFilm.getName());
        film.setDescription(dtoFilm.getDescription());
        film.setReleaseDate(dtoFilm.getReleaseDate());
        film.setDuration(dtoFilm.getDuration());

        return film;
    }
}

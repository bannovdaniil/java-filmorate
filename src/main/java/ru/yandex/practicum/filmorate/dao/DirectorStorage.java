package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.DtoDirector;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    Director create(DtoDirector dtoDirector);
    Director update(DtoDirector dtoDirector);
    void delete(int id);
    Director getById(int id);
    List<Director> getAll();

    List<Director> getDirectorsByFilm(Long film_id);

    List<Director> saveDirectorsOfFilm(Film film);

    List<Director> updateDirectorsOfFilm(Film film);

    void deleteDirectorsOfFilm(Long film_id);

    boolean isExists(int id);

    void validateDirector(int id);
}
